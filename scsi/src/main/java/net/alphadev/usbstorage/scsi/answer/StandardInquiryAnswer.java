/**
 * Copyright © 2014-2015 Jan Seeger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.alphadev.usbstorage.scsi.answer;

import net.alphadev.usbstorage.util.BitStitching;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
@SuppressWarnings("unused")
public class StandardInquiryAnswer {
    public static final byte LENGTH = 0x24;
    private final byte mResponseDataFormat;
    private final boolean mSccs;
    private final short mVersionDescriptor1;
    private final short mVersionDescriptor2;
    private final short mVersionDescriptor3;
    private byte mPeripheralQualifier;
    private byte mPeripheralDeviceType;
    private boolean mRemovable;
    private boolean mAerc;
    private boolean mNormAca;
    /**
     * Hierarchical Addressing Support
     */
    private boolean mHiSup;
    private byte mAdditionalLength;
    private String mVendorId;
    private String mProductId;
    private String mRevisionId;
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
        mVendorId = BitStitching.bytesToString(answer, 8, 8);
        mProductId = BitStitching.bytesToString(answer, 16, 16);
        mRevisionId = BitStitching.bytesToString(answer, 32, 4);
        mVersionDescriptor1 = 0;
        mVersionDescriptor2 = 0;
        mVersionDescriptor3 = 0;
        mResponseDataFormat = 0;
        mSccs = false;
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
