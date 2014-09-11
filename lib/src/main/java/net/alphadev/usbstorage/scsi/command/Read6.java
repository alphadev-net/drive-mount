package net.alphadev.usbstorage.scsi.command;

import net.alphadev.usbstorage.bbb.CommandBlockWrapper;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class Read6 extends ScsiCommand {
    public static final byte READ6 = 0x0;

    public Read6() {
        super(READ6);
    }

    @Override
    public CommandBlockWrapper.Direction getDirection() {
        return CommandBlockWrapper.Direction.DEVICE_TO_HOST;
    }

    @Override
    public byte[] asBytes() {
        return new byte[0];
    }

    @Override
    public int getExpectedAnswerLength() {
        return 0;
    }
}
