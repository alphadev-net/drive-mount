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

import java.nio.ByteOrder;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
@SuppressWarnings("unused")
public class RequestSenseResponse {
    public static final byte LENGTH = 18;

    /**
     * False indicates the information field not formatted according to SCSI standard.
     */
    private final boolean mValid;
    private final boolean mFilemark;
    private final boolean mEOM;
    private final boolean mILI;
    private final boolean mSKSV;

    private final ResponseCode mResponseCode;
    private final SenseKey mSenseKey;
    private final byte mAdditionalSenseLength;
    private final byte mAdditionalSenseCode;
    private final byte mAdditionalSenseQualifier;
    private final byte mFieldReplacableUnitCode;

    private final int mInformation;
    private final int mCommandSpecificInformation;

    /**
     * This field is only 3 byte long!
     */
    private final int mSenseKeySpecific;

    public RequestSenseResponse(byte[] answer) {
        mValid = (answer[0] & 0x80) == 0x80;
        mResponseCode = determineResponseCode((byte) (answer[0] & 0x7f));
        mFilemark = (answer[1] & 0x80) == 0x80;
        mEOM = (answer[1] & 0x40) == 0x40;
        mILI = (answer[1] & 0x20) == 0x20;
        mSenseKey = determineSenseKey((byte) (answer[1] & 0xf));
        mInformation = BitStitching.convertToInt(answer, 3, ByteOrder.BIG_ENDIAN);
        mAdditionalSenseLength = answer[7];
        mCommandSpecificInformation = BitStitching.convertToShort(answer, 8, ByteOrder.BIG_ENDIAN);
        mAdditionalSenseCode = answer[12];
        mAdditionalSenseQualifier = answer[13];
        mFieldReplacableUnitCode = answer[14];
        mSKSV = (answer[15] & 0x80) == 0x80;

        byte[] temp = new byte[]{0, (byte) (answer[15] & 0x7f), answer[16], answer[17]};
        mSenseKeySpecific = BitStitching.convertToInt(temp, 0, ByteOrder.BIG_ENDIAN);
    }

    private SenseKey determineSenseKey(byte senseKey) {
        switch (senseKey) {
            case 0x0:
                return SenseKey.NO_SENSE;
            case 0x1:
                return SenseKey.RECOVERED_ERROR;
            case 0x2:
                return SenseKey.NOT_READY;
            case 0x3:
                return SenseKey.MEDIUM_ERROR;
            case 0x4:
                return SenseKey.HARDWARE_ERROR;
            case 0x5:
                return SenseKey.ILLEGAL_REQUEST;
            case 0x6:
                return SenseKey.UNIT_ATTENTION;
            case 0x7:
                return SenseKey.DATA_PROTECT;
            case 0x8:
                return SenseKey.BLANK_CHECK;
            case 0x9:
                return SenseKey.VENDOR_SPECIFIC;
            case 0xa:
                return SenseKey.COPY_ABORTED;
            case 0xb:
                return SenseKey.ABORTED_COMMAND;
            case 0xc:
                return SenseKey.EQUAL;
            case 0xd:
                return SenseKey.VOLUME_OVERFLOW;
            case 0xe:
                return SenseKey.MISCOMPARE;
            default:
                throw new IllegalArgumentException("Don't know how to process the given Sense Key");
        }
    }


    private ResponseCode determineResponseCode(byte typeField) {
        switch (typeField) {
            case 0x70:
                return ResponseCode.CURRENT_ERROR;
            case 0x71:
                return ResponseCode.DEFERRED_ERROR;
            case 0x7f:
                return ResponseCode.VENDOR_SPECIFIC;
            default:
                return ResponseCode.RESERVED;
        }
    }

    public boolean isValid() {
        return mValid;
    }

    public boolean isFilemark() {
        return mFilemark;
    }

    public boolean isEOM() {
        return mEOM;
    }

    public boolean isILI() {
        return mILI;
    }

    public boolean isSKSV() {
        return mSKSV;
    }

    public ResponseCode getResponseCode() {
        return mResponseCode;
    }

    public SenseKey getSenseKey() {
        return mSenseKey;
    }

    public byte getAdditionalSenseLength() {
        return mAdditionalSenseLength;
    }

    public byte getAdditionalSenseCode() {
        return mAdditionalSenseCode;
    }

    public byte getAdditionalSenseQualifier() {
        return mAdditionalSenseQualifier;
    }

    public byte getFieldReplacableUnitCode() {
        return mFieldReplacableUnitCode;
    }

    public int getInformation() {
        return mInformation;
    }

    public int getCommandSpecificInformation() {
        return mCommandSpecificInformation;
    }

    public int getSenseKeySpecific() {
        return mSenseKeySpecific;
    }

    public static enum SenseKey {
        /**
         * Indicates that there is no specific sense key information to be reported for the
         * designated logical unit. This would be the case for a successful command or a command
         * that received CHECK CONDITION or COMMAND TERMINATED status because one of the filemark,
         * EOM, or ILI bits is set to one.
         * (0x0)
         */
        NO_SENSE,

        /**
         * Indicates that the last command completed successfully with some recovery action
         * performed by the target. Details may be determinable by examining the additional sense
         * bytes and the information field.  When multiple recovered errors occur during one
         * command, the choice of which error to report (first, last, most severe, etc.) is device
         * specific.
         * (0x1)
         */
        RECOVERED_ERROR,

        /**
         * Indicates that the logical unit addressed cannot be accessed. Operator intervention may
         * be required to correct this condition.
         * (0x2)
         */
        NOT_READY,

        /**
         * Indicates that the command terminated with a non-recovered error condition that was
         * probably caused by a flaw in the medium or an error in the recorded data.  This sense key
         * may also be returned if the target is unable to distinguish between a flaw in the medium
         * and a specific hardware failure (sense key 4h).
         * (0x3)
         */
        MEDIUM_ERROR,

        /**
         * Indicates that the target detected a non-recoverable hardware failure (for example,
         * controller failure, device failure, parity error, etc.) while performing the command or
         * during a self test.
         * (0x4)
         */
        HARDWARE_ERROR,

        /**
         * Indicates that there was an illegal parameter in the command descriptor block or in the
         * additional parameters supplied as data for some commands (FORMAT UNIT, SEARCH DATA,
         * etc.). If the target detects an invalid parameter in the command descriptor block, then
         * it shall terminate the command without altering the medium. If the target detects an
         * invalid parameter in the additional parameters supplied as data, then the target may have
         * already altered the medium. This sense key may also indicate that an invalid IDENTIFY
         * message was received (5.6.7).
         * (0x5)
         */
        ILLEGAL_REQUEST,

        /**
         * Indicates that the removable medium may have been changed or the target has been reset.
         * See 6.9 for more detailed information about the unit attention condition.
         * (0x6)
         */
        UNIT_ATTENTION,

        /**
         * Indicates that a command that reads or writes the medium was attempted on a block that is
         * protected from this operation. The read or write operation is not performed.
         * (0x7)
         */
        DATA_PROTECT,

        /**
         * Indicates that a write-once device or a sequential-access device encountered blank medium
         * or format-defined end-of-data indication while reading or a write-once device encountered
         * a non-blank medium while writing.
         * (0x8)
         */
        BLANK_CHECK,

        /**
         * This sense key is available for reporting vendor specific conditions.
         * (0x9)
         */
        VENDOR_SPECIFIC,

        /**
         * Indicates a COPY, COMPARE, or COPY AND VERIFY command was aborted due to an error
         * condition on the source device, the destination device, or both.  (See 7.2.3.2 for
         * additional information about this sense key.)
         * (0xa)
         */
        COPY_ABORTED,

        /**
         * Indicates that the target aborted the command. The initiator may be able to recover by
         * trying the command again.
         * (0xb)
         */
        ABORTED_COMMAND,

        /**
         * Indicates a SEARCH DATA command has satisfied an equal comparison.
         * (0xc)
         */
        EQUAL,

        /**
         * Indicates that a buffered peripheral device has reached the end-of-partition and data may
         * remain in the buffer that has not been written to the medium.  A RECOVER BUFFERED DATA
         * command(s) may be issued to read the unwritten data from the buffer.
         * (0xd)
         */
        VOLUME_OVERFLOW,

        /**
         * Indicates that the source data did not match the data read from the medium.
         * (0xe)
         */
        MISCOMPARE,

        /**
         * Reserved.
         * (0xf)
         */
        RESERVED

    }

    public static enum ResponseCode {
        CURRENT_ERROR,
        DEFERRED_ERROR,
        VENDOR_SPECIFIC,
        RESERVED
    }
}
