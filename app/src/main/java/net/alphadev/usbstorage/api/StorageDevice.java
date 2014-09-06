package net.alphadev.usbstorage.api;

@SuppressWarnings("unused")
public interface StorageDevice {

    public String getDeviceName();

    public StorageDetails getStorageDetails();

    public FsType getFsType();

    public static enum FsType {
        FAT
    }

    public static interface StorageDetails {
        public long getTotalSpace();

        public long getFreeSpace();
    }
}
