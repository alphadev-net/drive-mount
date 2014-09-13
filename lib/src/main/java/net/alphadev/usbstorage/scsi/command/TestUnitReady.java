package net.alphadev.usbstorage.scsi.command;

import net.alphadev.usbstorage.bbb.CommandBlockWrapper;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class TestUnitReady extends ScsiCommand {
    public static final byte TEST_UNIT_READY = 0x0;

    public TestUnitReady() {
        super(TEST_UNIT_READY);
    }

    @Override
    public byte[] asBytes() {
        // all zero since even opcode == 0x0
        return new byte[6];
    }

    @Override
    public int getExpectedAnswerLength() {
        return 0;
    }
}
