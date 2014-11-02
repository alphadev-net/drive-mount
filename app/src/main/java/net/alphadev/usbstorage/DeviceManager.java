package net.alphadev.usbstorage;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import net.alphadev.usbstorage.api.BulkDevice;

import java.io.IOException;

public class DeviceManager {

    public static final String ACTION_USB_PERMISSION = "ACTION_USB_PERMISSION";

    private static final String LOG_TAG = "Drive Mount";
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
    private final UsbManager mUsbManager;
    private final Context mContext;
    private StorageManager mStorageManager;
    private OnStorageChangedListener mStorageChangedListener;

    public DeviceManager(Context context, StorageManager storageManager) {
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        mStorageManager = storageManager;
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
        try {
            BulkDevice usbBulkDevice = new UsbBulkDevice(mContext, device);
            mStorageManager.tryMount(usbBulkDevice);
        } catch (IOException ex) {
            Log.d(LOG_TAG, "couldn't mount!", ex);
        } finally {
            notifyStorageChanged();
        }
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

    public static interface OnStorageChangedListener {
        public void onStorageChange();
    }
}
