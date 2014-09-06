package net.alphadev.usbstorage.api;

import java.io.Closeable;
import java.io.IOException;


/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public interface BulkDevice extends Closeable {
    int send_mass_storage_command(Transmittable command) throws IOException;

    byte[] retrieve_data_packet(int expected_length);

    boolean isClosed();
}
