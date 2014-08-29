package net.alphadev.usbstorage.scsi;

import net.alphadev.usbstorage.bbb.CommandBlockWrapper;
import static net.alphadev.usbstorage.util.BitStitching.setBytesFromShort;

/**
 * Created by jan on 29.08.14.
 */
public class ReadFormatCapacities extends ScsiCommand {
    public static final byte READ_FORMAT_CAPACITIES = 0x23;

    public ReadFormatCapacities() {
        super(READ_FORMAT_CAPACITIES);
    }

    @Override
    public CommandBlockWrapper.Direction getDirection() {
        return CommandBlockWrapper.Direction.DEVICE_TO_HOST;
    }

    @Override
    public byte[] asBytes() {
        byte[] retval = new byte[10];
        retval[0] = READ_FORMAT_CAPACITIES; // opcode
        setBytesFromShort((short)ReadFormatCapacitiesData.LENGTH, retval, 7);
        return retval;
    }

    @Override
    public int getExpectedAnswerLength() {
        return 0;
    }
}
