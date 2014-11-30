/**
 * Copyright © 2014 Jan Seeger
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

import net.alphadev.fat32wrapper.FatStorage;
import net.alphadev.usbstorage.api.BlockDevice;
import net.alphadev.usbstorage.api.BulkDevice;
import net.alphadev.usbstorage.api.StorageDevice;
import net.alphadev.usbstorage.bbb.BulkBlockDevice;
import net.alphadev.usbstorage.partition.MasterBootRecord;
import net.alphadev.usbstorage.partition.Partition;

import java.util.HashMap;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class StorageManager {
    private final HashMap<String, StorageDevice> mMountedDevices = new HashMap<>();

    public boolean tryMount(BulkDevice device) {
        BlockDevice blockDevice = new BulkBlockDevice(device);
        MasterBootRecord mbr = new MasterBootRecord(blockDevice);

        for (Partition partition : mbr.getPartitions()) {
            if (tryMountPartition(partition)) {
                return true;
            }
        }

        return false;
    }

    private boolean tryMountPartition(BlockDevice device) {
        if (mMountedDevices.get(device.getId()) != null) {
            // device seems already mounted… do nothing.
            return false;
        }

        StorageDevice storage = mountAsFatFS(device);

        if (storage != null) {
            System.out.println("Successfully mounted device: " + device.getId());
            mMountedDevices.put(device.getId(), storage);
            return true;
        }

        return false;
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

    public void removeAll(String deviceId) {
        if (mMountedDevices.containsKey(deviceId)) {
            mMountedDevices.remove(deviceId);
        }

        for (String key : mMountedDevices.keySet()) {
            if (key.startsWith(deviceId)) {
                mMountedDevices.remove(key);
            }
        }
    }
}
