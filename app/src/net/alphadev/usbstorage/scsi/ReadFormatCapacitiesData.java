package net.alphadev.usbstorage.scsi;

/**
 * Created by jan on 29.08.14.
 */
public class ReadFormatCapacitiesData {
    public static final int LENGTH = 12;

    public ReadFormatCapacitiesData(byte[] answer) {

    }

    public long getCapacity(int mediaIndex) {
        return 0;
    }
}
