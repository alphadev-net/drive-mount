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

import java.util.BitSet;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
@SuppressWarnings("unused")
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
            case 0x00:
                return MediumType.SCB_DEVICE;
        }
        return null;
    }

    public byte getModeDataLength() {
        return mModeDataLength;
    }

    public void setModeDataLength(byte modeDataLength) {
        mModeDataLength = modeDataLength;
    }

    public MediumType getMediumType() {
        return mMediumType;
    }

    public void setMediumType(MediumType mediumType) {
        mMediumType = mediumType;
    }

    public byte getDeviceSpecificParameter() {
        return mDeviceSpecificParameter;
    }

    public void setDeviceSpecificParameter(byte deviceSpecificParameter) {
        mDeviceSpecificParameter = deviceSpecificParameter;
    }

    public byte getBlockDescriptorLength() {
        return mBlockDescriptorLength;
    }

    public void setBlockDescriptorLength(byte blockDescriptorLength) {
        mBlockDescriptorLength = blockDescriptorLength;
    }

    public boolean getWriteProtection() {
        return mWriteProtection;
    }

    public void setWriteProtection(boolean writeProtection) {
        mWriteProtection = writeProtection;
    }

    public boolean getDPOFUA() {
        return mDPOFUA;
    }

    public void setDPOFUA(boolean dPOFUA) {
        mDPOFUA = dPOFUA;
    }

    public static enum MediumType {
        SCB_DEVICE
    }
}
