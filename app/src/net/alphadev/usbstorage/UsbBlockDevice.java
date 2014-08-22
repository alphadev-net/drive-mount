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

    private boolean readOnly;
    private boolean closed;

    public UsbBlockDevice(Context ctx, UsbDevice device) {
        final UsbManager manager = (UsbManager) ctx.getSystemService(Context.USB_SERVICE);
        open(device, manager);
    }

    private void open(UsbDevice device, UsbManager manager) {

        if (!manager.hasPermission(device)) {
            throw new IllegalStateException("You don't have the permission to access this device!");
        }

        for (int i = 0; i < device.getInterfaceCount(); i++) {
            UsbInterface interfaceProbe = device.getInterface(i);
            if (interfaceProbe.getInterfaceClass() == UsbConstants.USB_CLASS_MASS_STORAGE) {
                mDataInterface = device.getInterface(i);
            }
        }

        this.readOnly = false;
        for (int i = 0; i < mDataInterface.getEndpointCount(); i++) {
            UsbEndpoint endpointProbe = mDataInterface.getEndpoint(i);
            if (endpointProbe.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                if (endpointProbe.getDirection() == UsbConstants.USB_DIR_IN) {
                    mReadEndpoint = endpointProbe;
                } else {
                    if (!readOnly) {
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
            if (!request.queue(byteBuffer, mReadEndpoint.getMaxPacketSize())) {
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

    private static class CommandBlockWrapper {
        private static int tagCounter = 0;

        private byte[] signature;
        private int tag;
        private int dataTransferLength;
        private byte flags;
        private byte LUN;
        private byte cmdBlockLength;
        private byte[] cmdBlock;

        public CommandBlockWrapper() {
            cmdBlock = new byte[16];
            tag = tagCounter++;
        }

        public void setSignature(byte a, byte b, byte c, byte d) {
            signature = new byte[]{ a, b, c, d};
        }

        public byte[] asBytes() {
            return new byte[] {
                    signature[0],
                    signature[1],
                    signature[2],
                    signature[3],

                    (byte) tag,                 // first 8 bit of tag
                    (byte) (tag >>> 8),         // second 8 bit of tag
                    (byte) (tag >>> 16),        // third 8 bit of tag
                    (byte) (tag >>> 24),        // fourth 8 bit of tag

                    (byte) dataTransferLength,
                    (byte) (dataTransferLength >>> 8),
                    (byte) (dataTransferLength >>> 16),
                    (byte) (dataTransferLength >>> 24),

                    flags,
                    LUN,
                    cmdBlockLength,

                    cmdBlock[0], cmdBlock[1], cmdBlock[2], cmdBlock[3],
                    cmdBlock[4], cmdBlock[5], cmdBlock[6], cmdBlock[7],
                    cmdBlock[8], cmdBlock[9], cmdBlock[10], cmdBlock[11],
                    cmdBlock[12], cmdBlock[13], cmdBlock[14], cmdBlock[15]
            };
        }

        private static byte cdb_length[] = new byte[]{
                //	 0  1  2  3  4  5  6  7  8  9  A  B  C  D  E  F
                06, 06, 06, 06, 06, 06, 06, 06, 06, 06, 06, 06, 06, 06, 06, 06,  //  0
                06, 06, 06, 06, 06, 06, 06, 06, 06, 06, 06, 06, 06, 06, 06, 06,  //  1
                10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10,  //  2
                10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10,  //  3
                10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10,  //  4
                10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10,  //  5
                00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00,  //  6
                00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00,  //  7
                16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16,  //  8
                16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16,  //  9
                12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12,  //  A
                12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12,  //  B
                00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00,  //  C
                00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00,  //  D
                00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00,  //  E
                00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00,  //  F
        };
    }
}
