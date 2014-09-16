package net.alphadev.usbstorage.scsi.answer;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class ModeSenseResponse {
    public static final int LENGTH = 8;

    private byte mModeDataLength;
    private MediumType mMediumType;
    private byte mDeviceSpecificParameter;
    private byte mBlockDescriptorLength;

    public ModeSenseResponse(byte[] answer) {
        mModeDataLength = answer[0];
        mMediumType = determineMediumType(answer[1]);
    }

    private MediumType determineMediumType(byte typeField) {
        return null;
    }

    public static enum MediumType {

    }
}
