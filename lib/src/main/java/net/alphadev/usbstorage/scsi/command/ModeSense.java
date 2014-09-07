package net.alphadev.usbstorage.scsi.command;

import net.alphadev.usbstorage.bbb.CommandBlockWrapper;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class ModeSense extends ScsiCommand {
    public static final byte MODE_SENSE = 0x1a;

    public ModeSense() {
        super(MODE_SENSE);
    }

    @Override
    public CommandBlockWrapper.Direction getDirection() {
        return CommandBlockWrapper.Direction.DEVICE_TO_HOST;
    }

    @Override
    public byte[] asBytes() {
        byte[] retval = new byte[6];
        retval[0] = MODE_SENSE;
        return retval;
    }

    @Override
    public int getExpectedAnswerLength() {
        return 0;
    }
}
