package net.alphadev.usbstorage.scsi;

import net.alphadev.usbstorage.bbb.CommandBlockWrapper;

/**
 * Created by jan on 28.08.14.
 */
public class Inquiry extends ScsiCommand {
    public static final byte INQUIRY = 0x12;

    private boolean mCmdDt;
    private boolean mEvpd;

    public Inquiry() {
        super(INQUIRY);
    }

    @Override
    public byte[] asBytes() {
        byte[] buffer = new byte[6];
        buffer[0] = INQUIRY; // opcode
        if(mCmdDt) {buffer[1]+=2;}
        if(mEvpd) {buffer[1]+=1;}
        buffer[4] = StandardInquiryAnswer.LENGTH;    // LENGTH
        return buffer;
    }

    @Override
    public int getExpectedAnswerLength() {
        return StandardInquiryAnswer.LENGTH;
    }

    @Override
    public CommandBlockWrapper.Direction getDirection() {
        return CommandBlockWrapper.Direction.DEVICE_TO_HOST;
    }
}
