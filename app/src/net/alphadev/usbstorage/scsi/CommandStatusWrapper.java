package net.alphadev.usbstorage.scsi;

import java.nio.ByteBuffer;

/**
 * Created by jan on 27.08.14.
 */
public class CommandStatusWrapper {
    private byte[] mSignature;
    private int mTag;
    private int mDataResidue;
    private byte mStatus;

    public CommandStatusWrapper(byte[] data) {
        mSignature[0x0] = data[0x0];
        mSignature[0x1] = data[0x1];
        mSignature[0x2] = data[0x2];
        mSignature[0x3] = data[0x3];

        mTag = ByteBuffer.wrap(data, 0x4, 4).getInt();
        mDataResidue = ByteBuffer.wrap(data, 0x8, 4).getInt();
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
