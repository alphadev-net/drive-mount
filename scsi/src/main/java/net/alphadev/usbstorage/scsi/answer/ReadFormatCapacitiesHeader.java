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
package net.alphadev.usbstorage.scsi.answer;

import net.alphadev.usbstorage.util.BitStitching;

import java.nio.ByteOrder;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
@SuppressWarnings("unused")
public class ReadFormatCapacitiesHeader {
    public static final int LENGTH = 12;

    private byte mCapacityListLength;
    private int mNumberOfBlocks;
    private DescriptorType mDescriptorType;
    private int mBlockLength;

    public ReadFormatCapacitiesHeader(byte[] answer) {
        /** first three bits are reserved **/
        mCapacityListLength = answer[3];

        boolean hasValidNumOfEntries = mCapacityListLength % 8 == 0;
        int numOfEntries = mCapacityListLength / 8;
        if (!hasValidNumOfEntries || numOfEntries <= 0 || numOfEntries >= 256) {
            throw new IllegalArgumentException("Invalid CapacityListLength!");
        }

        mCapacityListLength = (byte) numOfEntries;
        mNumberOfBlocks = BitStitching.convertToInt(answer, 4, ByteOrder.BIG_ENDIAN);

        mDescriptorType = getDescriptorType(answer[5]);

        byte[] tempBlockLength = new byte[]{
                0, answer[9], answer[10], answer[11],
        };

        mBlockLength = BitStitching.convertToInt(tempBlockLength, 0, ByteOrder.BIG_ENDIAN);
    }

    /**
     * Extracts Bitflags from a given byte according to the following schema:
     * <p/>
     * 00b = Reserved.
     * 01b = Unformatted Media.
     * 10b = Formatted Media.
     * 11b = No Media Present.
     *
     * @param b byte holding the flags
     */
    private DescriptorType getDescriptorType(byte b) {
        switch (b) {
            case 1:
                return DescriptorType.UNFORMATTED_MEDIA;
            case 2:
                return DescriptorType.FORMATTED_MEDIA;
            case 3:
                return DescriptorType.NO_MEDIA_PRESENT;
            default:
                return DescriptorType.RESERVED;
        }
    }

    public int getCapacityEntryCount() {
        return mCapacityListLength;
    }

    public int getNumberOfBlocks() {
        return mNumberOfBlocks;
    }

    public DescriptorType getDescriptorTypes() {
        return mDescriptorType;
    }

    public int getBlockLength() {
        return mBlockLength;
    }

    public static enum DescriptorType {
        RESERVED,
        UNFORMATTED_MEDIA,
        FORMATTED_MEDIA,
        NO_MEDIA_PRESENT
    }
}
