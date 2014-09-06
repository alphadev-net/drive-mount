package net.alphadev.usbstorage.scsi;

import static net.alphadev.usbstorage.util.BitStitching.bytesToString;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
@SuppressWarnings("unused")
public class StandardInquiryAnswer {
    public static final byte LENGTH = 0x24;

    private byte mPeripheralQualifier;
    private byte mPeripheralDeviceType;
    private boolean mRemovable;
    private boolean mAerc;
    private boolean mNormAca;

    /**
     * Hierarchical Addressing Support
     */
    private boolean mHiSup;

    private byte mResponseDataFormat = 0;
    private byte mAdditionalLength;
    private boolean mSccs = false;
    private String mVendorId;
    private String mProductId;
    private String mRevisionId;
    private short mVersionDescriptor1 = 0;
    private short mVersionDescriptor2 = 0;
    private short mVersionDescriptor3 = 0;

    /**
     * Version 4 == SPC-2
     */
    private byte mVersion;

    public StandardInquiryAnswer(byte[] answer) {
        if (answer.length != LENGTH) {
            throw new IllegalArgumentException("Inquiry answer has invalid length!");
        }

        mPeripheralDeviceType = (byte) (answer[0] & 0xe0);
        mPeripheralQualifier = (byte) (answer[0] & 0x1f);
        mRemovable = answer[1] == (byte) 0x80;
        mVersion = answer[2];
        mAerc = answer[3] == (byte) 0x80;
        mNormAca = answer[3] == (byte) 0x20;
        mHiSup = answer[3] == (byte) 0x10;
        mAdditionalLength = answer[4];
        mRemovable = answer[5] == (byte) 0x80;
        mVendorId = bytesToString(answer, 8, 8);
        mProductId = bytesToString(answer, 16, 16);
        mRevisionId = bytesToString(answer, 32, 4);
    }

    public byte getAdditionalLength() {
        return mAdditionalLength;
    }

    public byte getPeripheralQualifier() {
        return mPeripheralQualifier;
    }

    public byte getPeripheralDeviceType() {
        return mPeripheralDeviceType;
    }

    public boolean isRemovable() {
        return mRemovable;
    }

    public boolean isAerc() {
        return mAerc;
    }

    public boolean isNormAca() {
        return mNormAca;
    }

    public boolean isHiSup() {
        return mHiSup;
    }

    public byte getResponseDataFormat() {
        return mResponseDataFormat;
    }

    public boolean isSccs() {
        return mSccs;
    }

    public String getVendorId() {
        return mVendorId;
    }

    public String getProductId() {
        return mProductId;
    }

    public String getRevisionId() {
        return mRevisionId;
    }

    public short getVersionDescriptor1() {
        return mVersionDescriptor1;
    }

    public short getVersionDescriptor2() {
        return mVersionDescriptor2;
    }

    public short getVersionDescriptor3() {
        return mVersionDescriptor3;
    }

    public byte getVersion() {
        return mVersion;
    }
}
