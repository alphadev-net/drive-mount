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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.provider.DocumentsContract;

import net.alphadev.fat32wrapper.FatStorage;
import net.alphadev.usbstorage.api.device.BlockDevice;
import net.alphadev.usbstorage.api.device.BulkDevice;
import net.alphadev.usbstorage.api.filesystem.Path;
import net.alphadev.usbstorage.api.filesystem.StorageDevice;
import net.alphadev.usbstorage.bbb.BulkBlockDevice;
import net.alphadev.usbstorage.partition.MasterBootRecord;
import net.alphadev.usbstorage.partition.Partition;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class StorageManager {
    private static final String AUTHORITY = "net.alphadev.usbstorage.documents";
    private static final String ACTION_UNMOUNT_DEVICE = "net.alphadev.usbstorage.ACTION_UNMOUNT_DEVICE";

    private final HashMap<String, StorageDevice> mMountedDevices = new HashMap<>();
    private final NotificationManager mNotificationManager;
    private final Context mContext;

    public StorageManager(Context context) {
        mContext = context;
        mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        final IntentFilter unmountFilter = new IntentFilter(ACTION_UNMOUNT_DEVICE);
        final BroadcastReceiver unmountReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                unmount(intent.getStringExtra("deviceId"));
            }
        };
        mContext.getApplicationContext().registerReceiver(unmountReceiver, unmountFilter);
    }

    public boolean tryMount(BulkDevice device) {
        final BlockDevice blockDevice = new BulkBlockDevice(device);
        blockDevice.initialize();
        final MasterBootRecord mbr = new MasterBootRecord(blockDevice);

        for (Partition partition : mbr.getPartitions()) {
            if (tryMountPartition(partition)) {
                return true;
            }
        }

        return false;
    }

    private boolean tryMountPartition(Partition device) {
        if (mMountedDevices.containsKey(device.getId())) {
            // device seems already mounted… do nothing.
            return false;
        }

        StorageDevice storage = firstTry(device);

        if (storage != null) {
            mMountedDevices.put(device.getId(), storage);
            postStorageNotification(storage);
            return true;
        }

        return false;
    }

    public void notifyStorageChanged() {
        mContext.getContentResolver()
                .notifyChange(DocumentsContract
                        .buildRootsUri(AUTHORITY), null);
    }

    private void postStorageNotification(StorageDevice device) {
        final String deviceName = mContext.getString(R.string.notification_title, device.getName());
        final String deviceInfo = mContext.getString(R.string.notification_content,
                device.getUnallocatedSpace(),
                device.getTotalSpace(),
                device.getType());
        final String unmountInfo = mContext.getString(R.string.notification_subtext);

        Notification.Builder builder = new Notification.Builder(mContext)
                .setContentTitle(deviceName)
                .setContentText(deviceInfo)
                .setSubText(unmountInfo)
                .setSmallIcon(R.drawable.drive_icon_gen)
                .setOngoing(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            builder.setLocalOnly(true)
                    .setGroup(AUTHORITY);
        }

        final Intent intent = new Intent(ACTION_UNMOUNT_DEVICE);
        intent.putExtra("deviceId", device.getId());

        builder.setContentIntent(
                PendingIntent.getBroadcast(mContext, 0, intent, 0));

        mNotificationManager.notify(device.getId(), 0, builder.build());
    }

    private StorageDevice firstTry(Partition device) {
        switch (device.getType()) {
            case FAT12:
            case FAT16:
            case FAT16_LARGE:
            case FAT16_LBA:
            case FAT32:
            case FAT32_LBA:
                return mountAsFatFS(device);
            default:
                return null;
        }
    }

    private StorageDevice mountAsFatFS(BlockDevice device) {
        try {
            return new FatStorage(device, true);
        } catch (Exception ex) {
            ex.printStackTrace();
            // at this point we tried to mount using the wrong fs type or the data is corrupt:
            // either way do not attempt to read any further.
            return null;
        }
    }

    public Iterable<? extends StorageDevice> getMounts() {
        return mMountedDevices.values();
    }

    public StorageDevice getDevice(Path path) {
        return mMountedDevices.get(path.getDeviceId());
    }

    public void unmount(String deviceId) {
        for (Map.Entry<String, StorageDevice> set : mMountedDevices.entrySet()) {
            if (set.getKey().startsWith(deviceId)) {
                try {
                    set.getValue().close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    mMountedDevices.remove(set.getKey());
                    mNotificationManager.cancel(set.getKey(), 0);
                }
            }
        }
        notifyStorageChanged();
    }
}
