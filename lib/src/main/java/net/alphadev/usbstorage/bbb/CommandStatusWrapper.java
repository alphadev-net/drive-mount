package net.alphadev.usbstorage.bbb;

import static net.alphadev.usbstorage.util.BitStitching.convertToInt;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class CommandStatusWrapper {
    private byte[] mSignature;
    private int mTag;
    private int mDataResidue;
    private byte mStatus;

    public CommandStatusWrapper(byte[] data) {
        if (data.length != 13) {
            throw new IllegalArgumentException("CSW always has a length of 13 bytes!");
        }

        mSignature = new byte[4];
        mSignature[0x0] = data[0x0];
        mSignature[0x1] = data[0x1];
        mSignature[0x2] = data[0x2];
        mSignature[0x3] = data[0x3];

        mTag = convertToInt(data, 0x4);
        mDataResidue = convertToInt(data, 0x8);
        mStatus = data[0xc];
    }

    public String getSignature() {
        return new String(mSignature);
    }

    public int getTag() {
        return mTag;
    }

    public int getDataResidue() {
        return mDataResidue;
    }

    public Status getStatus() {
        switch (mStatus) {
            case 0:
                return Status.COMMAND_PASSED;
            case 2:
                return Status.CHECK_CONDITION;
            case 8:
                return Status.BUSY;
            default:
                return Status.COMMAND_FAILED;
        }
    }

    public static enum Status {
        COMMAND_PASSED,
        COMMAND_FAILED,
        CHECK_CONDITION,
        BUSY
    }
}
