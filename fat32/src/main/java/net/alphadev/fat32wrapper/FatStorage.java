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

import de.waldheinz.fs.fat.FatFileSystem;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class FatStorage implements StorageDevice {
    private final String mId;
    private final FatFileSystem fs;

    public FatStorage(BlockDevice blockDevice, boolean readOnly) throws IOException {
        this.mId = blockDevice.getId();
        de.waldheinz.fs.BlockDevice wrapper = new FatBlockDeviceWrapper(blockDevice);
        fs = FatFileSystem.read(wrapper, readOnly);
    }

    @Override
    public String getName() {
        return fs.getVolumeLabel();
    }

    @Override
    public FileSystemProvider getProvider() {
        return new Fat32Provider(fs);
    }

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public long getTotalSpace() {
        return fs.getTotalSpace();
    }

    @Override
    public long getUnallocatedSpace() {
        return fs.getFreeSpace();
    }

    @Override
    public long getUsableSpace() {
        return fs.getUsableSpace();
    }

    @Override
    public boolean isReadOnly() {
        return fs.isReadOnly();
    }

    @Override
    public String getType() {
        return fs.getFatType().name();
    }

    @Override
    public void close() throws IOException {
        if (!isReadOnly()) {
            fs.flush();
        }

        fs.close();
    }
}
