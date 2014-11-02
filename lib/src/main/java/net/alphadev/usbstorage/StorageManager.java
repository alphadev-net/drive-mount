package net.alphadev.usbstorage;

import net.alphadev.usbstorage.api.BulkDevice;
import net.alphadev.usbstorage.api.StorageDevice;
import net.alphadev.usbstorage.bbb.BulkBlockDevice;
import net.alphadev.usbstorage.filesystems.FatStorage;

import java.util.HashMap;

import de.waldheinz.fs.BlockDevice;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class StorageManager {
    private final HashMap<Integer, StorageDevice> mMountedDevices = new HashMap<>();

    public void tryMount(BulkDevice device) {
        if (mMountedDevices.get(device.getId()) != null) {
            // device seems already mounted… do nothing.
            return;
        }

        StorageDevice storage = mountAsFatFS(device);
        mMountedDevices.put(device.getId(), storage);
    }

    private StorageDevice mountAsFatFS(BulkDevice device) {
        try {
            BlockDevice blockDevice = new BulkBlockDevice(device);
            return new FatStorage(blockDevice, true);
        } catch (Exception ex) {
            System.err.println(ex);
        }

        return null;
    }

    public Iterable<? extends StorageDevice> getMounts() {
        return mMountedDevices.values();
    }
}
