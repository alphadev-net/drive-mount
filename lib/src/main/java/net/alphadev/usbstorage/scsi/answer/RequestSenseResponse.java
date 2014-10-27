package net.alphadev.usbstorage.scsi.answer;

import net.alphadev.usbstorage.util.BitStitching;

import static net.alphadev.usbstorage.util.BitStitching.convertToInt;
import static net.alphadev.usbstorage.util.BitStitching.convertToShort;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class RequestSenseResponse {
    public static final byte LENGTH = 18;

    /**
     * False indicates the information field not formatted according to SCSI standard.
     */
    private boolean mValid;
    private boolean mFilemark;
    private boolean mEOM;
    private boolean mILI;
    private boolean mSKSV;

    private ResponseCode mResponseCode;
    private byte mSenseKey;
    private byte mAdditionalSenseLength;
    private byte mAdditionalSenseCode;
    private byte mAdditionalSenseQualifier;
    private byte mFieldReplacableUnitCode;

    private int mInformation;
    private int mCommandSpecificInformation;

    /**
     * This field is only 3 byte long!
     */
    private int mSenseKeySpecific;

    public RequestSenseResponse(byte[] answer) {
        mValid = (answer[0] & 0x80) == 0x80;
        mResponseCode = determineResponseCode((byte) (answer[0] & 0x7f));
        mFilemark = (answer[1] & 0x80) == 0x80;
        mEOM = (answer[1] & 0x40) == 0x40;
        mILI = (answer[1] & 0x20) == 0x20;
        mSenseKey = (byte) (answer[1] & 0xf);
        mInformation = convertToInt(answer, 3);
        mAdditionalSenseLength = answer[7];
        mCommandSpecificInformation = convertToShort(answer, 8);
        mAdditionalSenseCode = answer[12];
        mAdditionalSenseQualifier = answer[13];
        mFieldReplacableUnitCode = answer[14];
        mSKSV = (answer[15] & 0x80) == 0x80;

        byte[] temp = new byte[]{0, (byte) (answer[15]&0x7f), answer[16], answer[17]};
        mSenseKeySpecific = BitStitching.convertToInt(temp, 0);
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

    public enum ResponseCode {
        CURRENT_ERROR,
        DEFERRED_ERROR,
        VENDOR_SPECIFIC,
        RESERVED
    }
}
