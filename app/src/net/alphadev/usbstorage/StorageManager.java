package net.alphadev.usbstorage;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import de.waldheinz.fs.fat.FatFileSystem;

import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.HashMap;
import net.alphadev.usbstorage.impl.FatStorage;


public class StorageManager {

	public static final String ACTION_USB_PERMISSION = "ACTION_USB_PERMISSION";

	private final HashMap<UsbDevice, StorageDevice> mMountedDevices = new HashMap<>();

    private UsbManager mUsbManager;
    private Context mContext;
	private OnStorageChangedListener mStorageChangedListener;

    public StorageManager(Context context) {
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        mContext = context;
		
		IntentFilter permissionFilter = new IntentFilter(ACTION_USB_PERMISSION);
		context.registerReceiver(mPermissionReceiver, permissionFilter);

		IntentFilter attachmentFilter = new IntentFilter();
		attachmentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		attachmentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		context.registerReceiver(mAttachmentReceiver, attachmentFilter);
    }

	private final BroadcastReceiver mPermissionReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
			if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
				method(device);
			}
		}
	};

	private final BroadcastReceiver mAttachmentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			enumerateDevices();
		}
	};

	private void method(UsbDevice device) {
		mMountedDevices.put(device, null);

		if (mStorageChangedListener != null) {
			mStorageChangedListener.onStorageChange();
		}
	}

	public void setOnStorageChangedListener(OnStorageChangedListener listener) {
		mStorageChangedListener = listener;
	}
	
    public void enumerateDevices() {
		mMountedDevices.clear();

        for(UsbDevice device: mUsbManager.getDeviceList().values()) {
			if(mUsbManager.hasPermission(device)) {
				Log.d("Drive Mount", "App already has access to USB device");
				method(device);
			} else {
				Log.d("Drive Mount", "Requesting access to USB device");
				PendingIntent intent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
				mUsbManager.requestPermission(device, intent);
			}
        }
    }

	public Set<StorageDevice> getStorageDevices() {
		Set<StorageDevice> validDevices = new HashSet<>();
		
		for(StorageDevice device: mMountedDevices.values()) {
			if(device != null) {
				validDevices.add(device);
			}
		}
		return validDevices;
	}

    private StorageDevice mountAsFatFS(UsbDevice usbDevice) {
        try {
            UsbBlockDevice blockDevice = new UsbBlockDevice(mContext, usbDevice, true);
			return new FatStorage(blockDevice);
        } catch (Exception ex) {
            Log.d("asdf", "error while trying to mount fat volume", ex);
        }

        return null;
    }

	public static interface OnStorageChangedListener {
		public void onStorageChange();
	}
}
