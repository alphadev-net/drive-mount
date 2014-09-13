package net.alphadev.usbstorage.scsi.command;

import net.alphadev.usbstorage.bbb.CommandBlockWrapper;
import net.alphadev.usbstorage.scsi.answer.StandardInquiryAnswer;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
@SuppressWarnings("unused")
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
        if (mCmdDt) {
            buffer[1] += 2;
        }
        if (mEvpd) {
            buffer[1] += 1;
        }
        buffer[4] = StandardInquiryAnswer.LENGTH;    // LENGTH
        return buffer;
    }

    @Override
    public int getExpectedAnswerLength() {
        return StandardInquiryAnswer.LENGTH;
    }

    public boolean isCmdDt() {
        return mCmdDt;
    }

    public boolean isEvpd() {
        return mEvpd;
    }
}
