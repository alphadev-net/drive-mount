package net.alphadev.usbstorage.scsi.answer;

import static net.alphadev.usbstorage.util.BitStitching.convertToInt;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
@SuppressWarnings("unused")
public class ReadFormatCapacitiesEntry {
    public static final int LENGTH = 8;

    private int mNumOfBlocks;
    private FormatType mFormatType;
    private int mTypeDependentParameter;

    public ReadFormatCapacitiesEntry(byte[] answer) {
        mNumOfBlocks = convertToInt(answer, 0);
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
