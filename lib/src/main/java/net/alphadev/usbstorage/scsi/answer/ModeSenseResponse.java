package net.alphadev.usbstorage.scsi.answer;

import java.util.BitSet;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class ModeSenseResponse {
    public static final int LENGTH = 8;

    private byte mModeDataLength;
    private MediumType mMediumType;
    private byte mDeviceSpecificParameter;
    private byte mBlockDescriptorLength;
    private boolean mWriteProtection;
    private boolean mDPOFUA;

    public ModeSenseResponse(byte[] answer) {
        mModeDataLength = answer[0];
        mMediumType = determineMediumType(answer[1]);

        BitSet bs = new BitSet(answer[2]);
        mWriteProtection = bs.get(7);
        mDPOFUA = bs.get(4);

        mBlockDescriptorLength = answer[3];
    }

    private MediumType determineMediumType(byte typeField) {
        switch (typeField) {
            case 00:
                return MediumType.SCB_DEVICE;
        }
        return null;
    }

    public static enum MediumType {
        SCB_DEVICE
    }
}
