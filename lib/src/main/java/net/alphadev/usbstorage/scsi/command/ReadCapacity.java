package net.alphadev.usbstorage.scsi.command;

import net.alphadev.usbstorage.scsi.answer.ReadCapacityResponse;

import static net.alphadev.usbstorage.util.BitStitching.setBytesFromInt;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
@SuppressWarnings("unused")
public class ReadCapacity extends ScsiCommand {
    public static final byte READ_CAPACITY = 0x25;

    private int mLogicalBlockAddress;
    private byte mControl;

    public ReadCapacity() {
        super(READ_CAPACITY);
    }

    @Override
    public byte[] asBytes() {
        byte[] retval = new byte[10];
        retval[0] = READ_CAPACITY; // opcode
        // retval[1] is reserved
        setBytesFromInt(mLogicalBlockAddress, retval, 2);
        // retval[6-8] is reserved
        retval[9] = mControl;
        return retval;
    }


    @Override
    public int getExpectedAnswerLength() {
        return ReadCapacityResponse.LENGTH;
    }

    public int getLogicalBlockAddress() {
        return mLogicalBlockAddress;
    }

    public void setLogicalBlockAddress(int logicalBlockAddress) {
        this.mLogicalBlockAddress = logicalBlockAddress;
    }

    public void setControl(byte control) {
        this.mControl = control;
    }
}
