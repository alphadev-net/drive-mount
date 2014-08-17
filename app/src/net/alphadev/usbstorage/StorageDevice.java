package net.alphadev.usbstorage;

public interface StorageDevice {

    public String getDeviceName();

    public StorageDetails getStorageDetails();

    public FsType getFsType();

    public static interface StorageDetails {
        public long getTotalSpace();

        public long getFreeSpace();
    }

    public static enum FsType {
        FAT
    }
}
