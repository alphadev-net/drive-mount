package net.alphadev.usbstorage.filesystems;

import net.alphadev.usbstorage.api.StorageDevice;

import java.io.IOException;

import de.waldheinz.fs.BlockDevice;
import de.waldheinz.fs.fat.FatFileSystem;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class FatStorage implements StorageDevice {

    private final FatFileSystem fs;

    public FatStorage(BlockDevice blockDevice, boolean readOnly) throws IOException {
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
        switch (fs.getFatType()) {
            case FAT12:
                return FsType.FAT12;
            case FAT16:
                return FsType.FAT16;
            default:
                return FsType.FAT32;
        }
    }
}
