package net.alphadev.usbstorage;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import java.util.HashSet;
import java.util.Set;

import de.waldheinz.fs.fat.FatFileSystem;

public class StorageManager {

    private UsbManager mUsbManager;
    private Context mContext;

    public StorageManager(Context context) {
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        mContext = context;
    }

    public Set<StorageDevice> enumerateDevices() {
        HashSet<StorageDevice> validDevices = new HashSet<>();

        for(UsbDevice usbDevice: mUsbManager.getDeviceList().values()) {
            StorageDevice temp = mountAsFatFS(usbDevice);
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
            //
        }

        return null;
    }
}
