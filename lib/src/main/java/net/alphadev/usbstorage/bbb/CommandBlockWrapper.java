package net.alphadev.usbstorage.bbb;

import net.alphadev.usbstorage.api.Transmittable;

import java.nio.ByteOrder;

import static net.alphadev.usbstorage.util.BitStitching.setBytesFromInt;

/**
 * @author Jan Seeger <jan@alphadev.net>
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
        setBytesFromInt(++tagCounter, cwbData, 0x4, ByteOrder.BIG_ENDIAN);
    }



    public void setFlags(Direction directionFlags) {
        cwbData[0xc] = (byte) (directionFlags == Direction.DEVICE_TO_HOST ? 128 : 0);
    }

    public void setLun(byte lun) {
        cwbData[0xd] = lun;
    }

    public void setCommand(Transmittable command) {
        byte[] cmdBlock = command.asBytes();

        if (cmdBlock.length != 6 && cmdBlock.length != 10 &&
                cmdBlock.length != 12 && cmdBlock.length != 16) {
            throw new IllegalArgumentException("command has invalid size!");
        }

        int cmdOffset = 0xf;
        System.arraycopy(cmdBlock, 0, cwbData, cmdOffset, cmdBlock.length);

        cwbData[0xe] = (byte) cmdBlock.length;
        setBytesFromInt(command.getExpectedAnswerLength(), cwbData, 0x8);
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
