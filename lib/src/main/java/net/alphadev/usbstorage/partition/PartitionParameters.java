package net.alphadev.usbstorage.partition;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
@SuppressWarnings("unused")
public class PartitionParameters {
    private boolean mBootIndicator;
    private int mPartitionStart;
    private FileSystemDescriptor mDescriptor;
    private int mParititionEnd;
    private int mLogicalStart;
    private int mNumberOfSectors;

    public PartitionParameters(byte[] data) {
        mBootIndicator = data[0x0] == 0x80;
        // todo: calculate partition boundaries
        mDescriptor = FileSystemDescriptor.parse(data[4]);
    }

    public boolean isBootIndicator() {
        return mBootIndicator;
    }

    public int getPartitionStart() {
        return mPartitionStart;
    }

    public FileSystemDescriptor getDescriptor() {
        return mDescriptor;
    }

    public int getParititionEnd() {
        return mParititionEnd;
    }

    public int getLogicalStart() {
        return mLogicalStart;
    }

    public int getNumberOfSectors() {
        return mNumberOfSectors;
    }
}
