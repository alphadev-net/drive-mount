package net.alphadev.usbstorage;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import de.waldheinz.fs.fat.FatFileSystem;
import java.util.Iterator;

public class StorageManager {

	public static final String ACTION_USB_PERMISSION = "ACTION_USB_PERMISSION";

    private UsbManager mUsbManager;
    private Context mContext;
	private final Set<UsbDevice> mDeviceCache = new HashSet<>();

    public StorageManager(Context context) {
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        mContext = context;
		
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		context.registerReceiver(mUsbReceiver, filter);
    }

	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
			if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
				mDeviceCache.add(device);
			}
		}
	};

    public void enumerateDevices() {
		mDeviceCache.clear();
	
        for(UsbDevice usbDevice: mUsbManager.getDeviceList().values()) {
			if(mUsbManager.hasPermission(usbDevice)) {
				Log.d("Drive Mount", "App already has access to USB device");
				mDeviceCache.add(usbDevice);
			} else {
				Log.d("Drive Mount", "Requesting access to USB device");
				PendingIntent intent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
				mUsbManager.requestPermission(usbDevice, intent);
			}
        }
		
    }

	public Set<StorageDevice> getDevices() {
		HashSet<StorageDevice> validDevices = new HashSet<>();
		
		for(UsbDevice device: mDeviceCache) {
			StorageDevice temp = mountAsFatFS(device);
			if(temp != null) {
				validDevices.add(temp);
			}
		}
		return validDevices;
	}
    private StorageDevice mountAsFatFS(UsbDevice usbDevice) {
        try {
            UsbBlockDevice blockDevice = new UsbBlockDevice(mContext, usbDevice, true);
            final FatFileSystem fs = FatFileSystem.read(blockDevice, true);

            return new StorageDevice() {
                @Override
                public String getDeviceName() {
                    return fs.getVolumeLabel();
                }

                @Override
                public StorageDetails getStorageDetails() {
                    return new StorageDetails() {
                        @Override
                        public long getTotalSpace() {
                            return fs.getTotalSpace();
                        }

                        @Override
                        public long getFreeSpace() {
                            return fs.getFreeSpace();
                        }
                    };
                }

                @Override
                public FsType getFsType() {
                    return FsType.FAT;
                }
            };
        } catch (Exception ex) {
            Log.d("asdf", "error while mounting", ex);
        }

        return null;
    }
}
