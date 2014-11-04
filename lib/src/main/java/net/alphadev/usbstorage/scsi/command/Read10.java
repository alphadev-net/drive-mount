package net.alphadev.usbstorage.scsi.command;

import net.alphadev.usbstorage.scsi.CommandBlockWrapper;

import java.nio.ByteOrder;

import static net.alphadev.usbstorage.util.BitStitching.setBytesFromInt;
import static net.alphadev.usbstorage.util.BitStitching.setBytesFromShort;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class Read10 extends ScsiCommand {
    public static final byte READ10 = 0x28;

    private long offset;
    private short transferLength;
    private int mAnswerLength;

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
        setBytesFromInt((int) offset, bytes, 2, ByteOrder.BIG_ENDIAN);
        // 6 == group number
        setBytesFromShort(transferLength, bytes, 7, ByteOrder.BIG_ENDIAN);
        // 9 == control bits
        return bytes;
    }

    @Override
    public int getExpectedAnswerLength() {
        return mAnswerLength;
    }

    public void setExpectedAnswerLength(int length) {
        mAnswerLength = length;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public void setTransferLength(short transferLength) {
        this.transferLength = transferLength;
    }
}
