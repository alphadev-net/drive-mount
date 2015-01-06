/**
 * Copyright © 2014-2015 Jan Seeger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.alphadev.usbstorage.api.device;

import net.alphadev.usbstorage.api.Identifiable;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

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
     * @throws IOException              on write error
     * @throws IllegalArgumentException if the {@code devOffset} is negative
     *                                  or the write would go beyond the end of the device
     * @see #isReadOnly()
     */
    public abstract void write(long devOffset, ByteBuffer src)
            throws IOException,
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

    void initialize();
}
