package net.alphadev.usbstorage.filesystems;

import net.alphadev.usbstorage.api.StorageDevice;
import net.alphadev.usbstorage.bbb.UsbBlockDevice;

import java.io.IOException;

import de.waldheinz.fs.fat.FatFileSystem;

public class FatStorage implements StorageDevice {

    private final FatFileSystem fs;

    public FatStorage(UsbBlockDevice blockDevice, boolean readOnly) throws IOException {
        fs = FatFileSystem.read(blockDevice, readOnly);
    }

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
}
