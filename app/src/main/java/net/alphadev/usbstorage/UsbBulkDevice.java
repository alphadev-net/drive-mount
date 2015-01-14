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
package net.alphadev.usbstorage;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import net.alphadev.usbstorage.api.device.BulkDevice;
import net.alphadev.usbstorage.api.scsi.Transmittable;

import java.io.IOException;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class UsbBulkDevice implements BulkDevice {
    @SuppressWarnings("unused")
    private static final String LOG_TAG = "Drive Mount";
    private static final int TIMEOUT = 20000;

    private UsbEndpoint mReadEndpoint;
    private UsbEndpoint mWriteEndpoint;
    private UsbInterface mDataInterface;
    private UsbDeviceConnection mConnection;
    private int mDeviceId;
    private boolean closed;

    private UsbBulkDevice(UsbDevice device, UsbDeviceConnection connection,
                          UsbInterface dataInterface, UsbEndpoint in, UsbEndpoint out) {
        mDeviceId = device.getDeviceId();
        mConnection = connection;
        mDataInterface = dataInterface;
        mReadEndpoint = in;
        mWriteEndpoint = out;
        closed = false;
    }

    public static UsbBulkDevice read(Context ctx, UsbDevice device) {
        final UsbManager manager = (UsbManager) ctx.getSystemService(Context.USB_SERVICE);
        if (!manager.hasPermission(device)) {
            return null;
        }

        final UsbInterface dataInterface = findUsableInterface(device);
        if (dataInterface == null) {
            return null;
        }

        final UsbEndpoint in = findUsableEndpoints(dataInterface, UsbConstants.USB_DIR_IN);
        if (in == null) {
            return null;
        }

        final UsbEndpoint out = findUsableEndpoints(dataInterface, UsbConstants.USB_DIR_OUT);
        if (out == null) {
            return null;
        }

        final UsbDeviceConnection connection = openAndLockDevice(manager, device, dataInterface);
        if (connection == null) {
            return null;
        }

        return new UsbBulkDevice(device, connection, dataInterface, in, out);
    }

    private static UsbDeviceConnection openAndLockDevice(UsbManager manager, UsbDevice device, UsbInterface dataInterface) {
        final UsbDeviceConnection connection = manager.openDevice(device);
        if (connection.claimInterface(dataInterface, true)) {
            return connection;
        }

        return null;
    }

    private static UsbEndpoint findUsableEndpoints(UsbInterface usbInterface, int direction) {
        for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
            UsbEndpoint endpointProbe = usbInterface.getEndpoint(i);
            if (endpointProbe.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                if (endpointProbe.getDirection() == direction) {
                    return endpointProbe;
                }
            }
        }

        return null;
    }

    private static UsbInterface findUsableInterface(UsbDevice device) {
        for (int i = 0; i < device.getInterfaceCount(); i++) {
            UsbInterface interfaceProbe = device.getInterface(i);
            if (interfaceProbe.getInterfaceClass() == UsbConstants.USB_CLASS_MASS_STORAGE) {
                if (interfaceProbe.getInterfaceSubclass() == 0x6) {
                    if (interfaceProbe.getInterfaceProtocol() == 0x50) {
                        return device.getInterface(i);
                    }
                }
            }
        }

        return null;
    }

    @Override
    public int write(Transmittable command) {
        checkClosed();

        byte[] payload = command.asBytes();
        return mConnection.bulkTransfer(mWriteEndpoint, payload, payload.length, TIMEOUT);
    }

    @Override
    public byte[] read(int expected_length) {
        checkClosed();

        byte[] buffer = new byte[expected_length];
        mConnection.bulkTransfer(mReadEndpoint, buffer, buffer.length, TIMEOUT);
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

    @Override
    public String getId() {
        return Integer.valueOf(mDeviceId).toString();
    }
}
