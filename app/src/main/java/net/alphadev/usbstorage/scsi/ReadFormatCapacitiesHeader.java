package net.alphadev.usbstorage.scsi;

/**
 * Created by jan on 29.08.14.
 */
public class ReadFormatCapacitiesHeader {
    public static final int LENGTH = 12;

    public ReadFormatCapacitiesHeader(byte[] answer) {

    }

    public int getCapacityEntryCount() {
        return 0;
    }
}
