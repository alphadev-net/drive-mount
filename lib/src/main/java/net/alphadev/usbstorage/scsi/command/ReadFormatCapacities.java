package net.alphadev.usbstorage.scsi.command;

import java.nio.ByteOrder;

import static net.alphadev.usbstorage.util.BitStitching.setBytesFromShort;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class ReadFormatCapacities extends ScsiCommand {
    private static final byte READ_FORMAT_CAPACITIES = 0x23;

    @Override
    public byte[] asBytes() {
        byte[] retval = new byte[10];
        retval[0] = READ_FORMAT_CAPACITIES; // opcode
        setBytesFromShort((short) getExpectedAnswerLength(), retval, 7, ByteOrder.BIG_ENDIAN);
        return retval;
    }

    @Override
    public int getExpectedAnswerLength() {
        return 12;
    }
}
