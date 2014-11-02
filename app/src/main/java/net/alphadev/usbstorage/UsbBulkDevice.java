package net.alphadev.usbstorage;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import net.alphadev.usbstorage.api.BulkDevice;
import net.alphadev.usbstorage.api.Transmittable;

import java.io.IOException;

import static net.alphadev.usbstorage.util.BitStitching.bytesToHex;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class UsbBulkDevice implements BulkDevice {
    private static final String LOG_TAG = "Drive Mount";
    private static final int TIMEOUT = 20000;

    private UsbEndpoint mReadEndpoint;
    private UsbEndpoint mWriteEndpoint;
    private UsbInterface mDataInterface;
    private UsbDeviceConnection mConnection;
    private boolean closed;

    public UsbBulkDevice(Context ctx, UsbDevice device) throws IOException {
        final UsbManager manager = (UsbManager) ctx.getSystemService(Context.USB_SERVICE);

        if (!manager.hasPermission(device)) {
            throw new IllegalStateException("You don't have the permission to access this device!");
        }

        findUsableInterface(device);
        findUsableEndpoints();
        openAndLockDevice(device, manager);
    }

    private void openAndLockDevice(UsbDevice device, UsbManager manager) {
        mConnection = manager.openDevice(device);
        if (mConnection.claimInterface(mDataInterface, true)) {
            closed = false;
        }
    }

    private void findUsableEndpoints() {
        for (int i = 0; i < mDataInterface.getEndpointCount(); i++) {
            UsbEndpoint endpointProbe = mDataInterface.getEndpoint(i);
            if (endpointProbe.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                if (endpointProbe.getDirection() == UsbConstants.USB_DIR_IN) {
                    mReadEndpoint = endpointProbe;
                } else {
                    mWriteEndpoint = endpointProbe;
                }
            }
        }
    }

    private void findUsableInterface(UsbDevice device) {
        for (int i = 0; i < device.getInterfaceCount(); i++) {
            UsbInterface interfaceProbe = device.getInterface(i);
            if (interfaceProbe.getInterfaceClass() == UsbConstants.USB_CLASS_MASS_STORAGE) {
                if (interfaceProbe.getInterfaceSubclass() == 0x6) {
                    if (interfaceProbe.getInterfaceProtocol() == 0x50) {
                        mDataInterface = device.getInterface(i);
                    } else {
                        throw new UnsupportedOperationException("Cannot talk to this USB device!");
                    }
                } else {
                    throw new UnsupportedOperationException("Cannot talk to this USB device!");
                }
            }
        }
    }

    @Override
    public int write(Transmittable command) throws IOException {
        checkClosed();

        byte[] payload = command.asBytes();
        Log.d(LOG_TAG, "sending: " + bytesToHex(payload));
        return mConnection.bulkTransfer(mWriteEndpoint, payload, payload.length, TIMEOUT);
    }

    @Override
    public byte[] read(int expected_length) {
        checkClosed();

        byte[] buffer = new byte[expected_length];
        mConnection.bulkTransfer(mReadEndpoint, buffer, buffer.length, TIMEOUT);
        Log.d(LOG_TAG, "receiving: " + bytesToHex(buffer));
        return buffer;
    }

    private void checkClosed() {
        if (closed) throw new IllegalStateException("device already closed");
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() throws IOException {
        if (mConnection == null) {
            throw new IOException("Already closed");
        }

        mConnection.releaseInterface(mDataInterface);
        mConnection.close();
        mConnection = null;
        closed = true;
    }
}
