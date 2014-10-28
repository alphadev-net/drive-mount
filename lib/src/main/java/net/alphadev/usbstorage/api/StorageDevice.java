package net.alphadev.usbstorage.api;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
@SuppressWarnings("unused")
public interface StorageDevice {

    public String getDeviceName();

    public StorageDetails getStorageDetails();

    public FsType getFsType();

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
