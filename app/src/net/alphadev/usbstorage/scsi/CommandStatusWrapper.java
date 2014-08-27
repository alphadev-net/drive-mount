package net.alphadev.usbstorage.scsi;

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

        mTag = convertToInt(data, 0x4);
        mDataResidue = convertToInt(data, 0x8);
        mStatus = data[0xc];
    }

    private static int convertToInt(byte[] byteArray, int offset) {
        byte c1 = byteArray[offset + 3];
        byte c2 = byteArray[offset + 2];
        byte c3 = byteArray[offset + 1];
        byte c4 = byteArray[offset];

        long temp =
                ((0xFF & c1) << 24) | ((0xFF & c2) << 16) | ((0xFF & c3) << 8) | (0xFF & c4);

        return (int) (temp & 0x0FFFFFFFFL);
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
