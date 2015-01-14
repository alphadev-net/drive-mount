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
package net.alphadev.usbstorage.api.filesystem;

import net.alphadev.usbstorage.api.Identifiable;

import java.io.Closeable;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
@SuppressWarnings("unused")
public interface StorageDevice extends Identifiable, Closeable {
    /**
     * Returns the size, in bytes, of the file store.
     *
     * @return the size of the file store, in bytes
     */
    long getTotalSpace();

    /**
     * Returns the number of unallocated bytes in the file store.
     * <p/>
     * The returned number of unallocated bytes is a hint, but not a guarantee, that it is possible
     * to use most or any of these bytes. The number of unallocated bytes is most likely to be
     * accurate immediately after the space attributes are obtained. It is likely to be made
     * inaccurate by any external I/O operations including those made on the system outside of this
     * virtual machine.
     *
     * @return the number of unallocated bytes
     */
    long getUnallocatedSpace();

    /**
     * Returns the number of bytes available to this Java virtual machine on the file store.
     * <p/>
     * The returned number of available bytes is a hint, but not a guarantee, that it is possible to
     * use most or any of these bytes. The number of usable bytes is most likely to be accurate
     * immediately after the space attributes are obtained. It is likely to be made inaccurate by
     * any external I/O operations including those made on the system outside of this Java virtual
     * machine.
     *
     * @return the number of bytes available
     */
    long getUsableSpace();

    /**
     * Tells whether this file store is read-only.
     * <p/>
     * A file store is read-only if it does not support write operations or other changes to files.
     * Any attempt to create a file, open an existing file for writing etc. causes an IOException to
     * be thrown.
     *
     * @return true if, and only if, this file store is read-only
     */
    boolean isReadOnly();

    /**
     * Returns the type of this file store.
     * <p/>
     * The format of the string returned by this method is highly implementation specific.
     * It may indicate, for example, the format used or if the file store is local or remote.
     *
     * @return a string representing the type of this file store
     */
    String getType();

    /**
     * Returns the name of this file store.
     * <p/>
     * The format of the name is highly implementation specific.
     * It will typically be the name of the storage pool or volume.
     * The string returned by this method may differ from the string returned by the toString
     * method.
     *
     * @return the name of this file store
     */
    String getName();

    FileSystemProvider getProvider();
}
