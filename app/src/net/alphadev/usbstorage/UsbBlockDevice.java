package net.alphadev.usbstorage;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;

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
		open(device, manager);
    }

    private void open(UsbDevice device, UsbManager manager) {

        if(!manager.hasPermission(device)) {
            throw new IllegalStateException("You don't have the permission to access this device!");
        }

        for(int i = 0; i < device.getInterfaceCount(); i++) {
            UsbInterface interfaceProbe = device.getInterface(i);
            if(interfaceProbe.getInterfaceClass() == UsbConstants.USB_CLASS_MASS_STORAGE) {
                mDataInterface = device.getInterface(i);
            }
        }

        this.readOnly = false;
        for(int i = 0; i < mDataInterface.getEndpointCount(); i++) {
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

        final UsbRequest request = new UsbRequest();
        try {
            request.initialize(mConnection, mReadEndpoint);
            if (!request.queue(byteBuffer, byteBuffer.remaining())) {
                throw new IOException("Error queueing request.");
            }

            final UsbRequest response = mConnection.requestWait();
            if (mConnection.requestWait() != request) {
                throw new IOException("Null response");
            }
        } finally {
            request.close();
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
