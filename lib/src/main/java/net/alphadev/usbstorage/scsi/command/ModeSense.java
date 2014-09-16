package net.alphadev.usbstorage.scsi.command;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class ModeSense extends ScsiCommand {
    public static final byte MODE_SENSE = 0x1a;

    public ModeSense() {
        super(MODE_SENSE);
    }

    @Override
    public byte[] asBytes() {
        byte[] retval = new byte[6];
        retval[0] = MODE_SENSE; // opcode
        // 1 == dbd
        retval[2] = 0x3f;
        // 3 == =0 Return all subpage 00h mode pages in page_0 format
        retval[4] = (byte) 192;
        // 5 == control bit
        return retval;
    }

    @Override
    public int getExpectedAnswerLength() {
        return 0;
    }
}
