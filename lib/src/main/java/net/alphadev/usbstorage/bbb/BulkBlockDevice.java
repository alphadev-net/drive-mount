package net.alphadev.usbstorage.bbb;

import net.alphadev.usbstorage.api.BlockDevice;
import net.alphadev.usbstorage.api.BulkDevice;
import net.alphadev.usbstorage.scsi.answer.ModeSenseResponse;
import net.alphadev.usbstorage.scsi.answer.ReadCapacityResponse;
import net.alphadev.usbstorage.scsi.answer.ReadFormatCapacitiesEntry;
import net.alphadev.usbstorage.scsi.answer.ReadFormatCapacitiesHeader;
import net.alphadev.usbstorage.scsi.answer.RequestSenseResponse;
import net.alphadev.usbstorage.scsi.answer.StandardInquiryAnswer;
import net.alphadev.usbstorage.scsi.command.Inquiry;
import net.alphadev.usbstorage.scsi.command.ModeSense;
import net.alphadev.usbstorage.scsi.command.Read10;
import net.alphadev.usbstorage.scsi.command.ReadCapacity;
import net.alphadev.usbstorage.scsi.command.ReadFormatCapacities;
import net.alphadev.usbstorage.scsi.command.RequestSense;
import net.alphadev.usbstorage.scsi.command.ScsiCommand;
import net.alphadev.usbstorage.scsi.command.TestUnitReady;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import de.waldheinz.fs.ReadOnlyException;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class BulkBlockDevice implements BlockDevice {
    private BulkDevice mAbstractBulkDevice;
    private long mDeviceBoundaries;
    private int mBlockSize = 512;
    private byte mLunToUse;

    public BulkBlockDevice(BulkDevice usbBlockDevice) throws IOException {
        mAbstractBulkDevice = usbBlockDevice;

        setupInquiryPhase();
        testUnitReady();
        acquireDriveCapacity();
        senseMode();
        testUnitReady();
        mLunToUse = 0;
    }

    private void senseMode() throws IOException {
        ModeSense cmd = new ModeSense();
        cmd.setDisableBlockDescriptor(false);
        cmd.setPageControl(ModeSense.PageControlValues.Current);
        cmd.setPageCode((byte) 0x3f);
        cmd.setSubPageCode((byte) 0);
        send_mass_storage_command(cmd);

        byte[] data = mAbstractBulkDevice.read(cmd.getExpectedAnswerLength());
        new ModeSenseResponse(data);

        assumeDeviceStatusOK();
    }

    private void testUnitReady() throws IOException {
        send_mass_storage_command(new TestUnitReady());
        CommandStatusWrapper csw = getDeviceStatus();

        while (csw.getStatus() == CommandStatusWrapper.Status.BUSY) {
            testUnitReady();
        }

        assumeDeviceStatusOK(csw);
    }

    @SuppressWarnings("unused")
    private void acquireCardCapacities() throws IOException {
        try {
            send_mass_storage_command(new ReadFormatCapacities());
            byte[] answer = mAbstractBulkDevice.read(ReadFormatCapacitiesHeader.LENGTH);
            ReadFormatCapacitiesHeader capacity = new ReadFormatCapacitiesHeader(answer);
            assumeDeviceStatusOK();

            for (int i = 0; i < capacity.getCapacityEntryCount(); i++) {
                byte[] capacityData = mAbstractBulkDevice.read(ReadFormatCapacitiesEntry.LENGTH);
                new ReadFormatCapacitiesEntry(capacityData);
            }

            // determine the last addressable block
            if (capacity.getNumberOfBlocks() != 0) {
                mDeviceBoundaries = capacity.getNumberOfBlocks();
            }

            if (capacity.getBlockLength() != 0) {
                mBlockSize = capacity.getBlockLength();
            }
        } catch (IllegalArgumentException ex) {
            // do nothing as the read format capacities command is optional.
        }
    }

    private void acquireDriveCapacity() throws IOException {
        try {
            send_mass_storage_command(new ReadCapacity());
            byte[] answer = mAbstractBulkDevice.read(ReadCapacityResponse.LENGTH);
            ReadCapacityResponse capacity = new ReadCapacityResponse(answer);

            assumeDeviceStatusOK();

            mDeviceBoundaries = capacity.getNumberOfBlocks();
            mBlockSize = capacity.getBlockSize();
        } catch (IllegalArgumentException e) {
            // whaaat!
        }
    }

    private void assumeDeviceStatusOK() {
        assumeDeviceStatusOK(getDeviceStatus());
    }

    private void assumeDeviceStatusOK(CommandStatusWrapper csw) {
        if (CommandStatusWrapper.Status.COMMAND_PASSED != csw.getStatus()) {
            throw new IllegalStateException("device signaled error state!");
        }
    }

    @SuppressWarnings("unused")
    private void checkErrorCondition() throws IOException {
        send_mass_storage_command(new RequestSense());
        byte[] answer = mAbstractBulkDevice.read(RequestSenseResponse.LENGTH + 10);
        new RequestSenseResponse(answer);
    }

    private CommandStatusWrapper getDeviceStatus() {
        byte[] buffer = mAbstractBulkDevice.read(13);
        return new CommandStatusWrapper(buffer);
    }

    private void setupInquiryPhase() throws IOException {
        send_mass_storage_command(new Inquiry());

        byte[] answer = mAbstractBulkDevice.read(StandardInquiryAnswer.LENGTH);
        new StandardInquiryAnswer(answer);

        assumeDeviceStatusOK();
    }

    @Override
    public long getSize() throws IOException {
        /*
         * read during device setup.
         * won't change during the mount time.
         */
        return mDeviceBoundaries;
    }

    @Override
    public void read(long offset, ByteBuffer byteBuffer) throws IOException {
        final int requestSize = byteBuffer.limit();
        final int sectors = requestSize / getSectorSize();

        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.rewind();

        for (int current=0; current<sectors; current++) {
            System.out.printf("reading block %d of %d\n", current, sectors);
            Read10 cmd = new Read10();
            cmd.setOffset(offset + current * getSectorSize());
            cmd.setTransferLength((short) 1);
            cmd.setExpectedAnswerLength(getSectorSize());
            send_mass_storage_command(cmd);

            byte[] buffer = mAbstractBulkDevice.read(requestSize);
            byteBuffer.put(buffer);

            assumeDeviceStatusOK();
        }
    }

    private int send_mass_storage_command(ScsiCommand command) throws IOException {
        CommandBlockWrapper cbw = new CommandBlockWrapper();
        cbw.setFlags(command.getDirection());
        cbw.setLun(mLunToUse);
        cbw.setCommand(command);

        return mAbstractBulkDevice.write(cbw);
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
        return mBlockSize;
    }

    @Override
    public void close() throws IOException {
        mAbstractBulkDevice.close();
    }

    @Override
    public boolean isClosed() {
        return mAbstractBulkDevice.isClosed();
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public int getId() {
        return mAbstractBulkDevice.getId();
    }
}
