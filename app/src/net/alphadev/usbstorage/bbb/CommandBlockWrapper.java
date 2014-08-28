package net.alphadev.usbstorage.bbb;

import net.alphadev.usbstorage.api.Transmittable;

import static net.alphadev.usbstorage.util.BitStitching.setBytesFromInt;

/**
 * Created by jan on 26.08.14.
 */
public class CommandBlockWrapper implements Transmittable {
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
        setBytesFromInt(tagCounter, cwbData, 0x4, 4);
    }

    public void setFlags(Direction directionFlags) {
        cwbData[0xc] = (byte) (directionFlags == Direction.DEVICE_TO_HOST?128:0);
    }

    public void setLun(byte lun) {
        cwbData[0xd] = lun;
    }

    public void setCommand(Transmittable command) {
        byte[] cmdBlock = command.asBytes();

        if (cmdBlock.length > 16) {
            throw new IllegalArgumentException("command has invalid size!");
        }

        int cmdOffset = 0xf;
        for (int i = 0; i < cmdBlock.length; i++) {
            cwbData[i+cmdOffset] = cmdBlock[i];
        }

        cwbData[0xe] = (byte) cmdBlock.length;
        setBytesFromInt(command.getExpectedAnswerLength(), cwbData, 0x8, 4);
    }

    public byte[] asBytes() {
        return cwbData.clone();
    }

    @Override
    public int getExpectedAnswerLength() {
        return 0;
    }

    public enum Direction {
        HOST_TO_DEVICE,
        DEVICE_TO_HOST
    }
}
