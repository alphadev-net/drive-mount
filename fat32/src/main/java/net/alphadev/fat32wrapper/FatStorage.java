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
package net.alphadev.fat32wrapper;

import net.alphadev.usbstorage.api.device.BlockDevice;
import net.alphadev.usbstorage.api.filesystem.FileSystemProvider;
import net.alphadev.usbstorage.api.filesystem.StorageDevice;

import java.io.IOException;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class FatStorage implements StorageDevice {
    private final BlockDevice blockDevice;
    @SuppressWarnings("unused")
    private final boolean isReadOnly;

    public FatStorage(BlockDevice blockDevice, boolean readOnly) throws IOException {
        this.blockDevice = blockDevice;
        this.isReadOnly = readOnly;
        jniOpen(blockDevice);
    }

    @Override
    public String getName() {
        return jniGetName();
    }

    @Override
    public FileSystemProvider getProvider() {
        return new Fat32Provider(blockDevice);
    }

    @Override
    public String getId() {
        return blockDevice.getId();
    }

    @Override
    public long getTotalSpace() {
        return jniGetTotalSpace();
    }

    @Override
    public long getUnallocatedSpace() {
        return jniGetFreeSpace();
    }

    @Override
    public long getUsableSpace() {
        return jniGetUsableSpace();
    }

    @Override
    public boolean isReadOnly() {
        return isReadOnly;
    }

    @Override
    public String getType() {
        return jniGetType();
    }

    @Override
    public void close() throws IOException {
        jniClose();
    }

    private native void jniOpen(BlockDevice blockDevice);

    private native void jniClose();

    private native long jniGetFreeSpace();

    private native String jniGetName();

    private native long jniGetTotalSpace();

    private native String jniGetType();

    private native long jniGetUsableSpace();
}
