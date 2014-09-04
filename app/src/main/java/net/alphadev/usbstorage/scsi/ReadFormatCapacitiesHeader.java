package net.alphadev.usbstorage.scsi;

import static net.alphadev.usbstorage.util.BitStitching.convertToInt;

/**
 * Created by jan on 29.08.14.
 */
public class ReadFormatCapacitiesHeader {
    public static final int LENGTH = 12;

    private byte mCapacityListLength;
    private int mNumberOfBlocks;


    public ReadFormatCapacitiesHeader(byte[] answer) {
        /** first three bits are reserved **/
        mCapacityListLength = answer[3];
        mNumberOfBlocks = convertToInt(answer, 4);
    }

    public int getCapacityEntryCount() {
        return mCapacityListLength;
    }

    public static enum DescriptorTypes {
        RESERVED,
        UNFORMATTED_MEDIA,
        FORMATTED_MEDIA,
        NO_MEDIA_PRESENT
    }
}
