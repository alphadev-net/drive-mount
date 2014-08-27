package net.alphadev.usbstorage.scsi;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by jan on 27.08.14.
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

        mTag = ByteBuffer.wrap(data, 0x4, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
        mDataResidue = mTag = ByteBuffer.wrap(data, 0x8, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
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

    public byte getStatus() {
        return mStatus;
    }
}
