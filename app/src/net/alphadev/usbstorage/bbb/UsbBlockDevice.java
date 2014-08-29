package net.alphadev.usbstorage.bbb;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import net.alphadev.usbstorage.scsi.Inquiry;
import net.alphadev.usbstorage.scsi.ScsiCommand;
import net.alphadev.usbstorage.scsi.StandardInquiryAnswer;

import java.io.IOException;
import java.nio.ByteBuffer;

import de.waldheinz.fs.BlockDevice;
import de.waldheinz.fs.ReadOnlyException;

import static net.alphadev.usbstorage.util.BitStitching.bytesToHex;

public class UsbBlockDevice implements BlockDevice {

    public static final int DEFAULT_TRANSFER_SIZE = 512;
    private static final String LOG_TAG = "Drive Mount";
    private static final int TIMEOUT = 20000;

    private UsbEndpoint mReadEndpoint;
    private UsbEndpoint mWriteEndpoint;
    private UsbInterface mDataInterface;
    private UsbDeviceConnection mConnection;
    private boolean closed;
    private byte mLunToUse = 0;

    public UsbBlockDevice(Context ctx, UsbDevice device) throws IOException {
        final UsbManager manager = (UsbManager) ctx.getSystemService(Context.USB_SERVICE);
        open(device, manager);
    }

    private void open(UsbDevice device, UsbManager manager) throws IOException {
        if (!manager.hasPermission(device)) {
            throw new IllegalStateException("You don't have the permission to access this device!");
        }

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

        mConnection = manager.openDevice(device);
        if (mConnection.claimInterface(mDataInterface, true)) {
            closed = false;
        }

        setup();
    }

    @Override
    public long getSize() throws IOException {
        checkClosed();

        //int retval = send_mass_storage_command(GENERIC_USB.READ_CAPACITY_LENGTH);
        // create a usb request and ask for the drives size.
        // READ_CAPACITY_LENGTH

        return 0;
    }

    private void setup() throws IOException {
        send_mass_storage_command(new Inquiry());

        byte[] answer = retrieve_data_packet(StandardInquiryAnswer.LENGTH);
        StandardInquiryAnswer inquiryAnswer = new StandardInquiryAnswer(answer);

        CommandStatusWrapper csw = retrieve_mass_storage_answer();
        if(CommandStatusWrapper.Status.COMMAND_PASSED != csw.getStatus()) {
            throw new IllegalStateException("device signaled error state!");
        }

    }

    @Override
    public void read(long offset, ByteBuffer byteBuffer) throws IOException {
    }

    private int send_mass_storage_command(ScsiCommand command) throws IOException {
        checkClosed();

        CommandBlockWrapper cbw = new CommandBlockWrapper();
        cbw.setFlags(command.getDirection());
        cbw.setLun(mLunToUse);
        cbw.setCommand(command);

        byte[] payload = cbw.asBytes();
        Log.d(LOG_TAG, "sending: " + bytesToHex(payload));
        return mConnection.bulkTransfer(mWriteEndpoint, payload, payload.length, TIMEOUT);
    }

    private CommandStatusWrapper retrieve_mass_storage_answer() {
        byte[] buffer = retrieve_data_packet(13);
        return new CommandStatusWrapper(buffer);
    }

    private byte[] retrieve_data_packet(int expected_length) {
        checkClosed();

        byte[] buffer = new byte[expected_length];
        int result = mConnection.bulkTransfer(mReadEndpoint, buffer, buffer.length, TIMEOUT);
        Log.d(LOG_TAG, "receiving: " + bytesToHex(buffer));
        return buffer;
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
        return DEFAULT_TRANSFER_SIZE;
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
    public boolean isClosed() {
        return closed;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    private void checkClosed() {
        if (closed) throw new IllegalStateException("device already closed");
    }
}
