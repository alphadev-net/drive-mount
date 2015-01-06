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
public class ReadFormatCapacitiesEntry {
    public static final int LENGTH = 8;

    private final int mNumOfBlocks;
    private final FormatType mFormatType;
    private int mTypeDependentParameter;

    public ReadFormatCapacitiesEntry(byte[] answer) {
        mNumOfBlocks = BitStitching.convertToInt(answer, 0, ByteOrder.BIG_ENDIAN);
        mFormatType = determineType(answer[5]);
    }

    /**
     * Parses the DescriptorType field according to the specs defined in
     * http://www.rockbox.org/wiki/pub/Main/DataSheets/mmc2r11a.pdf to define the
     * TypeDependentParameters format.
     *
     * @param b input to parse
     * @return DescriptorType
     */
    private FormatType determineType(byte b) {
        switch (b) {
            case 0:
                return FormatType.BLOCK_LENGTH_IN_BYTES;
            case 4:
                return FormatType.ZONE_NUMBER_OF_THE_DESCRIPTION;
            case 5:
                return FormatType.LAST_ZONE_NUMBER;
            case 16:
            case 17:
            case 18:
                return FormatType.FIXED_PACKET_SIZE_IN_SECTORS;
            case 32:
                return FormatType.SPARING_PARAMETERS;
            default:
                return FormatType.RESERVED;
        }
    }

    public int getNumOfBlocks() {
        return mNumOfBlocks;
    }

    public FormatType getFormatType() {
        return mFormatType;
    }

    public int getTypeDependentParameter() {
        return mTypeDependentParameter;
    }

    public static enum FormatType {
        BLOCK_LENGTH_IN_BYTES,
        ZONE_NUMBER_OF_THE_DESCRIPTION,
        LAST_ZONE_NUMBER,
        FIXED_PACKET_SIZE_IN_SECTORS,
        SPARING_PARAMETERS,
        RESERVED
    }
}
