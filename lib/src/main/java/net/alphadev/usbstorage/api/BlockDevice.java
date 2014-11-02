package net.alphadev.usbstorage.api;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

import de.waldheinz.fs.ReadOnlyException;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public interface BlockDevice extends Identifiable, Closeable {
    /**
     * Gets the total length of this device in bytes.
     *
     * @return the total number of bytes on this device
     * @throws java.io.IOException on error getting the size of this device
     */
    public abstract long getSize() throws IOException;

    /**
     * Read a block of data from this device.
     *
     * @param devOffset the byte offset where to read the data from
     * @param dest      the destination buffer where to store the data read
     * @throws IOException on read error
     */
    public abstract void read(long devOffset, ByteBuffer dest)
            throws IOException;

    /**
     * Writes a block of data to this device.
     *
     * @param devOffset the byte offset where to store the data
     * @param src       the source {@code ByteBuffer} to write to the device
     * @throws de.waldheinz.fs.ReadOnlyException if this {@code BlockDevice} is read-only
     * @throws IOException                       on write error
     * @throws IllegalArgumentException          if the {@code devOffset} is negative
     *                                           or the write would go beyond the end of the device
     * @see #isReadOnly()
     */
    public abstract void write(long devOffset, ByteBuffer src)
            throws ReadOnlyException, IOException,
            IllegalArgumentException;

    /**
     * Flushes data in caches to the actual storage.
     *
     * @throws IOException on write error
     */
    public abstract void flush() throws IOException;

    /**
     * Returns the size of a sector on this device.
     *
     * @return the sector size in bytes
     * @throws IOException on error determining the sector size
     */
    public int getSectorSize() throws IOException;

    /**
     * Closes this {@code BlockDevice}. No methods of this device may be
     * accesses after this method was called.
     *
     * @throws IOException on error closing this device
     * @see #isClosed()
     */
    public void close() throws IOException;

    /**
     * Checks if this device was already closed. No methods may be called
     * on a closed device (except this method).
     *
     * @return if this device is closed
     */
    public boolean isClosed();

    /**
     * Checks if this {@code BlockDevice} is read-only.
     *
     * @return if this {@code BlockDevice} is read-only
     */
    public boolean isReadOnly();
}
