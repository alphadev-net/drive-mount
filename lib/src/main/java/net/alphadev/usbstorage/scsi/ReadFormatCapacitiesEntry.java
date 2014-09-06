package net.alphadev.usbstorage.scsi;

/**
 *
 * @author Jan Seeger <jan@alphadev.net>
 */
public class ReadFormatCapacitiesEntry {
    public static final int LENGTH = 8;

    public ReadFormatCapacitiesEntry(byte[] answer) {

    }

    public long getCapacity(int mediaIndex) {
        return 0;
    }
}
