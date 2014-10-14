package net.alphadev.usbstorage;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import net.alphadev.usbstorage.api.StorageDevice;
import net.alphadev.usbstorage.bbb.BulkBlockDevice;
import net.alphadev.usbstorage.filesystems.FatStorage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.waldheinz.fs.BlockDevice;


public class StorageManager {

    public static final String ACTION_USB_PERMISSION = "ACTION_USB_PERMISSION";

    private static final String LOG_TAG = "Drive Mount";

    private final HashMap<UsbDevice, StorageDevice> mMountedDevices = new HashMap<>();
    private final BroadcastReceiver mPermissionReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                tryMount(device);
            }
        }
    };
    private final BroadcastReceiver mAttachmentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            enumerateDevices();
        }
    };
    private final BroadcastReceiver mDetachmentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // code to cleanly unmount drive
        }
    };
    private UsbManager mUsbManager;
    private Context mContext;
    private OnStorageChangedListener mStorageChangedListener;

    public StorageManager(Context context) {
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        mContext = context;

        IntentFilter permissionFilter = new IntentFilter(ACTION_USB_PERMISSION);
        context.registerReceiver(mPermissionReceiver, permissionFilter);

        IntentFilter attachmentFilter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        context.registerReceiver(mAttachmentReceiver, attachmentFilter);

        IntentFilter detachmentFilter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED);
        context.registerReceiver(mDetachmentReceiver, detachmentFilter);

        enumerateDevices();
    }

    private void tryMount(UsbDevice device) {
        if (mMountedDevices.get(device) != null) {
            // device seems already mounted… do nothing.
            return;
        }

        StorageDevice storage = mountAsFatFS(device);
        mMountedDevices.put(device, storage);
        notifyStorageChanged();
    }

    private void notifyStorageChanged() {
        if (mStorageChangedListener != null) {
            mStorageChangedListener.onStorageChange();
        }
    }

    public void setOnStorageChangedListener(OnStorageChangedListener listener) {
        mStorageChangedListener = listener;
    }

    private void enumerateDevices() {
        mMountedDevices.clear();

        for (UsbDevice device : mUsbManager.getDeviceList().values()) {
            if (!mUsbManager.hasPermission(device)) {
                Log.d(LOG_TAG, "Requesting access to USB device");
                PendingIntent intent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
                mUsbManager.requestPermission(device, intent);
                continue;
            }

            Log.d(LOG_TAG, "App already has access to USB device");
            tryMount(device);
        }
    }

    public Set<StorageDevice> getStorageDevices() {
        Set<StorageDevice> validDevices = new HashSet<>();

        for (StorageDevice device : mMountedDevices.values()) {
            if (device != null) {
                validDevices.add(device);
            }
        }

        return validDevices;
    }

    private StorageDevice mountAsFatFS(UsbDevice usbDevice) {
        try {
            UsbBulkDevice usbBulkDevice = new UsbBulkDevice(mContext, usbDevice);
            BlockDevice blockDevice = new BulkBlockDevice(usbBulkDevice);
            return new FatStorage(blockDevice, true);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "error while trying to mount fat volume", ex);
        }

        return null;
    }

    public static interface OnStorageChangedListener {
        public void onStorageChange();
    }
}
