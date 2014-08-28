package net.alphadev.usbstorage.scsi;

import static net.alphadev.usbstorage.util.BitStitching.bytesToString;

/**
 * Created by jan on 28.08.14.
 */
public class StandardInquiryAnswer {
    private byte mPeripheralQualifier;
    private byte mPeripheralDeviceType;
    private boolean mRemovable;
    private boolean mAerc;
    private boolean mNormAca;
    private boolean mHiSup;
    private byte mResponseDataFormat;
    private byte mAdditionalLength;
    private boolean mSccs;
    private String mVendorId;
    private String mProductId;
    private String mRevisionId;
    private short mVersionDescriptor1;
    private short mVersionDescriptor2;
    private short mVersionDescriptor3;

    /**
     * Version 4 == SPC-2
     */
    private byte mVersion;

    public StandardInquiryAnswer(byte[] answer) {
        if(answer.length != Inquiry.LENGTH) {
            throw new IllegalArgumentException("Inquiry answer has invalid length!");
        }

        mPeripheralDeviceType = (byte) (answer[0]&0xe0);
        mPeripheralQualifier = (byte) (answer[0]&0x1f);
        mRemovable = answer[1]==(byte)0x80;
        mVersion = answer[2];
        mAerc = answer[3]==(byte)0x80;
        mNormAca = answer[3]==(byte)0x20;
        mHiSup = answer[3]==(byte)0x10;
        mAdditionalLength = answer[4];
        mRemovable = answer[5]==(byte)0x80;
        mVendorId = bytesToString(answer, 8, 8);
        mProductId = bytesToString(answer, 16, 16);
        mRevisionId = bytesToString(answer, 32, 4);
    }
}
