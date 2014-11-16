package net.alphadev.usbstorage.partition;

import net.alphadev.usbstorage.api.BlockDevice;
import net.alphadev.usbstorage.util.HashCodeUtil;

import java.io.IOException;
import java.nio.ByteBuffer;

import de.waldheinz.fs.ReadOnlyException;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class Partition implements BlockDevice {
    private final BlockDevice mDevice;
    private final int mPartitionOffset;
    private final PartitionParameters mParameter;

    public Partition(BlockDevice device, int partitionOffset, PartitionParameters param) {
        mDevice = device;
        mPartitionOffset = partitionOffset;
        mParameter = param;
    }

    @Override
    public long getSize() throws IOException {
        return mParameter.getNumberOfSectors() * mDevice.getSectorSize();
    }

    @Override
    public void read(long devOffset, ByteBuffer dest) throws IOException {
        long newOffset = mParameter.getLogicalStart() + devOffset;
        mDevice.read(newOffset, dest);
    }

    @Override
    public void write(long devOffset, ByteBuffer src) throws ReadOnlyException, IOException, IllegalArgumentException {
        long newOffset = mParameter.getLogicalStart() + devOffset;
        mDevice.read(newOffset, src);
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
    public int getId() {
        return HashCodeUtil.getHashCode(mDevice.getId(), mPartitionOffset);
    }
}
