package net.alphadev.usbstorage.scsi.command;

import net.alphadev.usbstorage.bbb.CommandBlockWrapper;

import static net.alphadev.usbstorage.util.BitStitching.setBytesFromShort;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class Read10 extends ScsiCommand {
    public static final byte READ10 = 0x8;

    private long offset;

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
        setBytesFromShort((short) offset, bytes, 2);
        return bytes;
    }

    @Override
    public int getExpectedAnswerLength() {
        return 0;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }
}
