package net.alphadev.usbstorage;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidParameterException;

import de.waldheinz.fs.BlockDevice;
import de.waldheinz.fs.ReadOnlyException;

public class UsbBlockDevice implements BlockDevice {

    public final static int DEFAULT_SECTOR_SIZE = 512;

    private UsbEndpoint mReadEndpoint;
    private UsbEndpoint mWriteEndpoint;
    private UsbInterface mDataInterface;
    private UsbDeviceConnection mConnection;

    private final Object mReadBufferLock = new Object();
    private final Object mWriteBufferLock = new Object();

    private boolean readOnly;
    private boolean closed;

    public UsbBlockDevice(Context ctx, UsbDevice device, final boolean readOnly) {
        final UsbManager manager = (UsbManager) ctx.getSystemService(Context.USB_SERVICE);
    }

    private void open(UsbDevice device, UsbManager manager) {
        for(int i = 0; i <= device.getInterfaceCount(); i++) {
            UsbInterface interfaceProbe = device.getInterface(i);
            if(interfaceProbe.getInterfaceClass() == UsbConstants.USB_CLASS_MASS_STORAGE) {
                mDataInterface = device.getInterface(i);
            }
        }

        this.readOnly = false;
        for(int i = 0; i <= mDataInterface.getEndpointCount(); i++) {
            UsbEndpoint endpointProbe = mDataInterface.getEndpoint(i);
            if(endpointProbe.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                if(endpointProbe.getDirection() == UsbConstants.USB_DIR_IN) {
                    mReadEndpoint = endpointProbe;
                } else {
                    if(!readOnly) {
                        mWriteEndpoint = endpointProbe;
                        this.readOnly = false;
                    }
                }
            }
        }

        mConnection = manager.openDevice(device);
        closed = false;
    }

    @Override
    public long getSize() throws IOException {
        checkClosed();

        // create a usb request and ask for the drives size.

        return 0;
    }

    @Override
    public void read(long offset, ByteBuffer byteBuffer) throws IOException {
        checkClosed();

        final int numBytesRead;

        synchronized (mReadBufferLock) {
            if(!byteBuffer.hasArray()) {
                throw new InvalidParameterException("Your Buffer isn't Array-backed!");
            }

            int readAmt = (int)Math.min(getSize(), (long)byteBuffer.remaining());
            int timeoutMillis = 250;
            numBytesRead = mConnection.bulkTransfer(mReadEndpoint, byteBuffer.array(), readAmt, timeoutMillis);

            if (numBytesRead < 0) {
                // This sucks: we get -1 on timeout, not 0 as preferred.
                // We *should* use UsbRequest, except it has a bug/api oversight
                // where there is no way to determine the number of bytes read
                // in response :\ -- http://b.android.com/28023
                if (timeoutMillis == Integer.MAX_VALUE) {
                    // Hack: Special case "~infinite timeout" as an error.
                    throw new IllegalStateException("Unknown usb timing error occured!");
                }
                throw new IllegalStateException("Unknown error occured!");
            }
            //System.arraycopy(mReadBuffer, 0, dest, 0, numBytesRead);
        }
    }

    @Override
    public void write(long offset, ByteBuffer byteBuffer) throws ReadOnlyException, IOException, IllegalArgumentException {

    }

    @Override
    public void flush() throws IOException {
        // since we write everything directly to the device there's no need to flush.
    }

    @Override
    public int getSectorSize() throws IOException {
        return DEFAULT_SECTOR_SIZE;
    }

    @Override
    public void close() throws IOException {
        if (mConnection == null) {
            throw new IOException("Already closed");
        }
        mConnection.close();
        mConnection = null;
        closed = true;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    private void checkClosed() {
        if (closed) throw new IllegalStateException("device already closed");
    }
}
