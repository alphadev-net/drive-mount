package net.alphadev.usbstorage.scsi.command;

import net.alphadev.usbstorage.api.ScsiTransferable;
import net.alphadev.usbstorage.scsi.CommandBlockWrapper;

/**
 * Class that communicates using SCSI Transparent Command Set as specified by:
 * http://www.13thmonkey.org/documentation/SCSI/spc2r20.pdf
 *
 * @author Jan Seeger <jan@alphadev.net>
 */
public abstract class ScsiCommand implements ScsiTransferable {
    protected final byte mOpCode;

    public ScsiCommand(byte opCode) {
        mOpCode = opCode;
    }

    public CommandBlockWrapper.Direction getDirection() {
        return CommandBlockWrapper.Direction.DEVICE_TO_HOST;
    }
}
