package net.alphadev.usbstorage;

import net.alphadev.usbstorage.api.BlockDevice;
import net.alphadev.usbstorage.api.BulkDevice;
import net.alphadev.usbstorage.api.StorageDevice;
import net.alphadev.usbstorage.bbb.BulkBlockDevice;
import net.alphadev.usbstorage.filesystems.FatStorage;
import net.alphadev.usbstorage.partition.MasterBootRecord;
import net.alphadev.usbstorage.partition.Partition;
import net.alphadev.usbstorage.util.BlockDeviceWrapper;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class StorageManager {
    private final HashMap<Integer, StorageDevice> mMountedDevices = new HashMap<>();

    public void tryMount(BulkDevice device) {
        try {
            BlockDevice blockDevice = new BulkBlockDevice(device);
            MasterBootRecord mbr = new MasterBootRecord(blockDevice);

            for (Partition partition : mbr.getPartitions()) {
                tryMountPartition(partition);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void tryMountPartition(BlockDevice device) {
        if (mMountedDevices.get(device.getId()) != null) {
            // device seems already mounted… do nothing.
            return;
        }

        StorageDevice storage = mountAsFatFS(device);

        mMountedDevices.put(device.getId(), storage);
    }

    private StorageDevice mountAsFatFS(BlockDevice device) {
        try {
            de.waldheinz.fs.BlockDevice wrapper = new BlockDeviceWrapper(device);
            return new FatStorage(wrapper, true);
        } catch (Exception ex) {
            // don't do shit as it could also be a different fs format.
        }

        return null;
    }

    public Iterable<? extends StorageDevice> getMounts() {
        return mMountedDevices.values();
    }
}
