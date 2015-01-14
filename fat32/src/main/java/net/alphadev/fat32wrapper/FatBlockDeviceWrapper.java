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

import java.io.IOException;
import java.nio.ByteBuffer;

import de.waldheinz.fs.ReadOnlyException;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public final class FatBlockDeviceWrapper implements de.waldheinz.fs.BlockDevice {
    private final BlockDevice mDevice;

    public FatBlockDeviceWrapper(BlockDevice device) {
        mDevice = device;
    }

    @Override
    public long getSize() throws IOException {
        return mDevice.getSize();
    }

    @Override
    public void read(long devOffset, ByteBuffer dest) throws IOException {
        mDevice.read(devOffset, dest);
    }

    @Override
    public void write(long devOffset, ByteBuffer src) throws ReadOnlyException, IOException, IllegalArgumentException {
        mDevice.write(devOffset, src);
    }

    @Override
    public void flush() throws IOException {
        mDevice.flush();
    }

    @Override
    public int getSectorSize() throws IOException {
        return mDevice.getSectorSize();
    }

    @Override
    public void close() throws IOException {
        mDevice.close();
    }

    @Override
    public boolean isClosed() {
        return mDevice.isClosed();
    }

    @Override
    public boolean isReadOnly() {
        return mDevice.isReadOnly();
    }
}
