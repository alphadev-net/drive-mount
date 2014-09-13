package net.alphadev.usbstorage.scsi.command;

import net.alphadev.usbstorage.api.Transmittable;
import net.alphadev.usbstorage.bbb.CommandBlockWrapper;

/**
 * Class that communicates using SCSI Transparent Command Set as specified by:
 * http://www.13thmonkey.org/documentation/SCSI/spc2r20.pdf
 *
 * @author Jan Seeger <jan@alphadev.net>
 */
public abstract class ScsiCommand implements Transmittable {
    protected final byte mOpCode;

    public ScsiCommand(byte opCode) {
        mOpCode = opCode;
    }

    public CommandBlockWrapper.Direction getDirection() {
        return CommandBlockWrapper.Direction.DEVICE_TO_HOST;
    }
}
