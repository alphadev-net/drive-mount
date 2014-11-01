package net.alphadev.usbstorage.api;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
@SuppressWarnings("unused")
public interface StorageDevice {

    String getDeviceName();

    StorageDetails getStorageDetails();

    FsType getFsType();

    public static enum FsType {
        FAT12,
        FAT16,
        FAT32
    }

    public static interface StorageDetails {
        public long getTotalSpace();

        public long getFreeSpace();
    }
}
