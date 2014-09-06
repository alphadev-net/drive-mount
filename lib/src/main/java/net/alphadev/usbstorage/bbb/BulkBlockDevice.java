package net.alphadev.usbstorage.bbb;

import net.alphadev.usbstorage.api.BulkDevice;
import net.alphadev.usbstorage.scsi.Inquiry;
import net.alphadev.usbstorage.scsi.ReadFormatCapacities;
import net.alphadev.usbstorage.scsi.ReadFormatCapacitiesEntry;
import net.alphadev.usbstorage.scsi.ReadFormatCapacitiesHeader;
import net.alphadev.usbstorage.scsi.ScsiCommand;
import net.alphadev.usbstorage.scsi.StandardInquiryAnswer;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

import de.waldheinz.fs.BlockDevice;
import de.waldheinz.fs.ReadOnlyException;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class BulkBlockDevice implements BlockDevice, Closeable {
    private BulkDevice mAbstractBulkDevice;
    private long mDeviceBoundaries;
    private int mBlockSize = 512;
    private byte mLunToUse = 0;

    public BulkBlockDevice(BulkDevice usbBlockDevice) throws IOException {
        mAbstractBulkDevice = usbBlockDevice;

        setupInquiryPhase();
        setupCapacityPhase();
    }

    private void setupCapacityPhase() throws IOException {
        ReadFormatCapacities cmd = new ReadFormatCapacities();
        send_mass_storage_command(cmd);
        byte[] answer = mAbstractBulkDevice.retrieve_data_packet(cmd.getExpectedAnswerLength());
        ReadFormatCapacitiesHeader capacity = new ReadFormatCapacitiesHeader(answer);

        CommandStatusWrapper csw = retrieve_mass_storage_answer();
        if (CommandStatusWrapper.Status.COMMAND_PASSED != csw.getStatus()) {
            throw new IllegalStateException("device signaled error state!");
        }

        for (int i = 0; i < capacity.getCapacityEntryCount(); i++) {
            byte[] capacityData = mAbstractBulkDevice.retrieve_data_packet(ReadFormatCapacitiesEntry.LENGTH);
            ReadFormatCapacitiesEntry rce = new ReadFormatCapacitiesEntry(capacityData);
        }

        // determine the last addressable block
        mDeviceBoundaries = capacity.getNumberOfBlocks();
        mBlockSize = capacity.getBlockLength();
    }

    private void setupInquiryPhase() throws IOException {
        send_mass_storage_command(new Inquiry());

        byte[] answer = mAbstractBulkDevice.retrieve_data_packet(StandardInquiryAnswer.LENGTH);
        new StandardInquiryAnswer(answer);

        CommandStatusWrapper csw = retrieve_mass_storage_answer();
        if (CommandStatusWrapper.Status.COMMAND_PASSED != csw.getStatus()) {
            throw new IllegalStateException("device signaled error state!");
        }
    }

    private CommandStatusWrapper retrieve_mass_storage_answer() {
        byte[] buffer = mAbstractBulkDevice.retrieve_data_packet(13);
        return new CommandStatusWrapper(buffer);
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
    }

    private int send_mass_storage_command(ScsiCommand command) throws IOException {
        CommandBlockWrapper cbw = new CommandBlockWrapper();
        cbw.setFlags(command.getDirection());
        cbw.setLun(mLunToUse);
        cbw.setCommand(command);

        return mAbstractBulkDevice.send_mass_storage_command(cbw);
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
}
