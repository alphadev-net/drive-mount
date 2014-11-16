package net.alphadev.usbstorage.scsi.answer;

import java.nio.ByteOrder;

import static net.alphadev.usbstorage.util.BitStitching.convertToInt;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class ReadCapacityResponse {
    public static final int LENGTH = 8;

    private final int mBlockSize;
    private final int mNumberOfBlocks;

    public ReadCapacityResponse(byte[] answer) {
        mNumberOfBlocks = convertToInt(answer, 0, ByteOrder.BIG_ENDIAN);
        mBlockSize = convertToInt(answer, 4, ByteOrder.BIG_ENDIAN);
    }

    public int getBlockSize() {
        return mBlockSize;
    }

    public int getNumberOfBlocks() {
        return mNumberOfBlocks;
    }
}
