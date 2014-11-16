package net.alphadev.usbstorage.api;

import java.io.Closeable;

/**
 * A bulk device is an abstract device which communicates using SCSI.
 *
 * @author Jan Seeger <jan@alphadev.net>
 */
public interface BulkDevice extends Closeable, Identifiable {
    /**
     * Transmits a given payload to the device BulkDevice being represented.
     *
     * @param payload to transfer
     * @return the amount actually sent
     */
    int write(Transmittable payload);

    /**
     * Receives a payload of a given length from the BulkDevice being represented.
     *
     * @param length of the payload
     * @return the payload data
     */
    byte[] read(int length);

    /**
     * @return true if the connection to the BulkDevice being represented has already been disengaged.
     */
    boolean isClosed();
}
