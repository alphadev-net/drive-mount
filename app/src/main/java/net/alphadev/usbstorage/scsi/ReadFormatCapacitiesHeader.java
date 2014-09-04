package net.alphadev.usbstorage.scsi;

/**
 * Created by jan on 29.08.14.
 */
public class ReadFormatCapacitiesHeader {
    public static final int LENGTH = 12;

    private byte mCapacityListLength;

    public ReadFormatCapacitiesHeader(byte[] answer) {
        /** first three bits are reserved **/
        mCapacityListLength = answer[3];


    }

    public int getCapacityEntryCount() {
        return mCapacityListLength;
    }
}
