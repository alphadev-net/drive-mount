package net.alphadev.usbstorage.scsi.command;

import net.alphadev.usbstorage.bbb.CommandBlockWrapper;

import static net.alphadev.usbstorage.util.BitStitching.setBytesFromInt;
import static net.alphadev.usbstorage.util.BitStitching.setBytesFromShort;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class Read10 extends ScsiCommand {
    public static final byte READ10 = 0x8;

    private long offset;
    private short requestSize;

    public Read10() {
        super(READ10);
    }

    @Override
    public CommandBlockWrapper.Direction getDirection() {
        return CommandBlockWrapper.Direction.DEVICE_TO_HOST;
    }

    @Override
    public byte[] asBytes() {
        final byte[] bytes = new byte[10];
        bytes[0] = READ10; // opcode
        // 1 == flags
        setBytesFromInt((int) offset, bytes, 2);
        // 6 == group number
        setBytesFromShort(requestSize, bytes, 7);
        // 9 == control bits
        return bytes;
    }

    @Override
    public int getExpectedAnswerLength() {
        return 0;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public void setRequestSize(short requestSize) {
        this.requestSize = requestSize;
    }
}
