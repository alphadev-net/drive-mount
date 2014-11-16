package net.alphadev.usbstorage.scsi.command;

import net.alphadev.usbstorage.scsi.answer.RequestSenseResponse;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class RequestSense extends ScsiCommand {
    private static final byte REQUEST_SENSE = 0x3;

    @Override
    public byte[] asBytes() {
        byte[] retval = new byte[10];
        retval[0] = REQUEST_SENSE; // opcode
        // byte 1-3: reserved
        retval[4] = RequestSenseResponse.LENGTH; // answer length
        // byte 5: control flags
        return retval;
    }

    @Override
    public int getExpectedAnswerLength() {
        return RequestSenseResponse.LENGTH;
    }
}
