/**
 * Copyright © 2014-2015 Jan Seeger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.alphadev.usbstorage.partition;

import net.alphadev.usbstorage.util.BitStitching;

import java.nio.ByteOrder;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
@SuppressWarnings("unused")
public class PartitionParameters {
    private final byte mPartitionOffset;
    private final boolean mBootIndicator;
    private final FileSystemDescriptor mDescriptor;
    private final HeadSectorCylinder mPartitionStart;
    private final HeadSectorCylinder mPartitionEnd;
    private final int mLogicalStart;
    private final int mNumberOfSectors;

    public PartitionParameters(byte[] data, byte offset) {
        mPartitionOffset = offset;
        mBootIndicator = data[0x0] == 0x80;
        mPartitionStart = new HeadSectorCylinder(data, 0x1);
        mDescriptor = FileSystemDescriptor.parse(data[4]);
        mPartitionEnd = new HeadSectorCylinder(data, 0x5);
        mLogicalStart = BitStitching.convertToInt(data, 0x8, ByteOrder.LITTLE_ENDIAN);
        mNumberOfSectors = BitStitching.convertToInt(data, 0xc, ByteOrder.LITTLE_ENDIAN);
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

    public byte getPartitionOffset() {
        return mPartitionOffset;
    }

    public static class HeadSectorCylinder {
        private final byte mHead;
        private final byte mSector;
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
