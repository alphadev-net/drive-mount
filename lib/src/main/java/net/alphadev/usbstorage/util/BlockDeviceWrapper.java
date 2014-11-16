package net.alphadev.usbstorage.util;

import net.alphadev.usbstorage.api.BlockDevice;

import java.io.IOException;
import java.nio.ByteBuffer;

import de.waldheinz.fs.ReadOnlyException;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class BlockDeviceWrapper implements de.waldheinz.fs.BlockDevice {
    private final BlockDevice mDevice;

    public BlockDeviceWrapper(BlockDevice device) {
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
