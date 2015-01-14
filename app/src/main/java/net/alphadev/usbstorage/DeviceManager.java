/**
 * Copyright © 2014-2015 Jan Seeger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.alphadev.usbstorage;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import net.alphadev.usbstorage.api.device.BulkDevice;

public final class DeviceManager {
    private static final String ACTION_USB_PERMISSION = "net.alphadev.usbstorage.ACTION_USB_PERMISSION";
    private final UsbManager mUsbManager;
    private final Context mContext;
    private final StorageManager mStorageManager;

    public DeviceManager(Context context, StorageManager storageManager) {
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        mStorageManager = storageManager;
        mContext = context;

        IntentFilter permissionFilter = new IntentFilter(ACTION_USB_PERMISSION);
        BroadcastReceiver mPermissionReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    tryMount(device);
                }
            }
        };
        context.registerReceiver(mPermissionReceiver, permissionFilter);

        IntentFilter attachmentFilter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        BroadcastReceiver mAttachmentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                enumerateDevices();
            }
        };
        context.registerReceiver(mAttachmentReceiver, attachmentFilter);

        IntentFilter detachmentFilter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED);
        BroadcastReceiver mDetachmentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                unmountRemovedDevices(device);
            }
        };
        context.registerReceiver(mDetachmentReceiver, detachmentFilter);

        enumerateDevices();
    }

    private void unmountRemovedDevices(UsbDevice device) {
        String deviceId = Integer.valueOf(device.getDeviceId()).toString();
        mStorageManager.unmount(deviceId);
        mStorageManager.notifyStorageChanged();
    }

    private void tryMount(UsbDevice device) {
        final BulkDevice usbBulkDevice = UsbBulkDevice.read(mContext, device);
        if (usbBulkDevice != null && mStorageManager.tryMount(usbBulkDevice)) {
            mStorageManager.notifyStorageChanged();
        }
    }

    private void enumerateDevices() {
        for (UsbDevice device : mUsbManager.getDeviceList().values()) {
            if (!mUsbManager.hasPermission(device)) {
                PendingIntent intent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
                mUsbManager.requestPermission(device, intent);
                continue;
            }

            tryMount(device);
        }
    }
}
