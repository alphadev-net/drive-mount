package net.alphadev.usbstorage.scsi;

/**
 * Created by jan on 31.08.14.
 */
public class ReadFormatCapacitiesEntry {
    public static final int LENGTH = 8;

    public ReadFormatCapacitiesEntry(byte[] answer) {

    }

    public long getCapacity(int mediaIndex) {
        return 0;
    }
}
