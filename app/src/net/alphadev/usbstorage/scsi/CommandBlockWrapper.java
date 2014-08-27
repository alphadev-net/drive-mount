package net.alphadev.usbstorage.scsi;

/**
 * Created by jan on 26.08.14.
 */
public class CommandBlockWrapper {
    private static int tagCounter = 0;
    private final byte[] cwbData;

    public CommandBlockWrapper() {
        cwbData = new byte[0x1f];

        // set CBW signature
        cwbData[0x0] = 'U';
        cwbData[0x1] = 'S';
        cwbData[0x2] = 'B';
        cwbData[0x3] = 'C';

        // increase and write tag counter
        tagCounter++;
        cwbData[0x4] = (byte) tagCounter;                 // first 8 bit of tag
        cwbData[0x5] = (byte) (tagCounter >>> 8);         // second 8 bit of tag
        cwbData[0x6] = (byte) (tagCounter >>> 16);        // third 8 bit of tag
        cwbData[0x7] = (byte) (tagCounter >>> 24);        // fourth 8 bit of tag
    }

    public void setTransferLength(int transferLength) {
        cwbData[0x8] = (byte) transferLength;
        cwbData[0x9] = (byte) (transferLength >>> 8);
        cwbData[0xa] = (byte) (transferLength >>> 16);
        cwbData[0xb] = (byte) (transferLength >>> 24);
    }

    public void setFlags(Direction directionFlags) {
        cwbData[0xc] = (byte) (directionFlags == Direction.DEVICE_TO_HOST?128:0);
    }

    public void setLun(byte lun) {
        cwbData[0xd] = lun;
    }

    public void setCommand(byte[] cmdBlock) {
        if (cmdBlock.length > 16) {
            throw new IllegalArgumentException("command has invalid size!");
        }

        for (int i = 0; i < cmdBlock.length; i++) {
            cwbData[i] = cmdBlock[i];
        }

        cwbData[0xe] = (byte) cmdBlock.length;
    }

    public byte[] asBytes() {
        return cwbData.clone();
    }

    public enum Direction {
        HOST_TO_DEVICE,
        DEVICE_TO_HOST
    }
}
