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
package net.alphadev.usbstorage.partition;

import net.alphadev.usbstorage.api.device.BlockDevice;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class Partition implements BlockDevice {
    private final BlockDevice mDevice;
    private final PartitionParameters mParameter;

    public Partition(BlockDevice device, PartitionParameters param) {
        mDevice = device;
        mParameter = param;
    }

    @Override
    public long getSize() throws IOException {
        return mParameter.getNumberOfSectors() * mDevice.getSectorSize();
    }

    @Override
    public void read(long devOffset, ByteBuffer dest) throws IOException {
        long newOffset = mParameter.getLogicalStart() * mDevice.getSectorSize() + devOffset;
        mDevice.read(newOffset, dest);
    }

    @Override
    public void write(long devOffset, ByteBuffer src) throws IOException, IllegalArgumentException {
        // don't try to write anything while offset calculation is off!
        //long newOffset = mParameter.getLogicalStart() + devOffset;
        //mDevice.read(newOffset, src);
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

    @Override
    public void initialize() {
        // no need to initialize the partition
    }

    public FileSystemDescriptor getType() {
        return mParameter.getDescriptor();
    }

    @Override
    public String getId() {
        return mDevice.getId() + ':' + Integer.toString(mParameter.getPartitionOffset());
    }
}
