package net.alphadev.usbstorage.api;

import java.io.Closeable;
import java.io.IOException;

/**
 * A bulk device is an abstract device which communicates using SCSI.
 *
 * @author Jan Seeger <jan@alphadev.net>
 */
public interface BulkDevice extends Closeable, Identifiable {
    int write(Transmittable command) throws IOException;

    byte[] read(int expected_length);

    boolean isClosed();
}
