package net.alphadev.usbstorage.partition;

import net.alphadev.usbstorage.util.BitStitching;

import java.nio.ByteOrder;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
@SuppressWarnings("unused")
public class PartitionParameters {
    private boolean mBootIndicator;
    private FileSystemDescriptor mDescriptor;
    private HeadSectorCylinder mPartitionStart;
    private HeadSectorCylinder mPartitionEnd;
    private int mLogicalStart;
    private int mNumberOfSectors;

    public PartitionParameters(byte[] data) {
        mBootIndicator = data[0x0] == 0x80;
        mPartitionStart = new HeadSectorCylinder(data, 0x1);
        mDescriptor = FileSystemDescriptor.parse(data[4]);
        mPartitionEnd = new HeadSectorCylinder(data, 0x5);
        mLogicalStart = BitStitching.convertToInt(data, 0x8, ByteOrder.LITTLE_ENDIAN);
        mNumberOfSectors = BitStitching.convertToInt(data, 0xc, ByteOrder.LITTLE_ENDIAN);

        if (mDescriptor == FileSystemDescriptor.UNUSED) {
            throw new IllegalArgumentException();
        }
    }

    public boolean isBootable() {
        return mBootIndicator;
    }

    public HeadSectorCylinder getPartitionStart() {
        return mPartitionStart;
    }

    public FileSystemDescriptor getDescriptor() {
        return mDescriptor;
    }

    public HeadSectorCylinder getParititionEnd() {
        return mPartitionEnd;
    }

    public int getLogicalStart() {
        return mLogicalStart;
    }

    public int getNumberOfSectors() {
        return mNumberOfSectors;
    }

    public static class HeadSectorCylinder {
        private byte mHead;
        private byte mSector;
        private short mCylinder;

        public HeadSectorCylinder(byte[] hscData, int offset) {
            mHead = hscData[offset];
            mSector = (byte) (hscData[offset + 1] & 0x3F);
            mCylinder = (short) (hscData[offset + 1] & 0xC0);
            mCylinder = (short) (mCylinder << 6);
            mCylinder += hscData[offset + 2];
        }

        public byte getHead() {
            return mHead;
        }

        public byte getSector() {
            return mSector;
        }

        public short getmCylinder() {
            return mCylinder;
        }
    }
}
