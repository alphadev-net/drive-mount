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
package net.alphadev.usbstorage.scsi.command;

import net.alphadev.usbstorage.scsi.answer.ReadCapacityResponse;
import net.alphadev.usbstorage.util.BitStitching;

import java.nio.ByteOrder;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
@SuppressWarnings("unused")
public class ReadCapacity extends ScsiCommand {
    public static final byte READ_CAPACITY = 0x25;

    private int mLogicalBlockAddress;
    private byte mControl;

    @Override
    public byte[] asBytes() {
        byte[] retval = new byte[10];
        retval[0] = READ_CAPACITY; // opcode
        // retval[1] is reserved
        BitStitching.setBytesFromInt(mLogicalBlockAddress, retval, 2, ByteOrder.BIG_ENDIAN);
        // retval[6-8] is reserved
        retval[9] = mControl;
        return retval;
    }

    @Override
    public int getExpectedAnswerLength() {
        return ReadCapacityResponse.LENGTH;
    }

    public int getLogicalBlockAddress() {
        return mLogicalBlockAddress;
    }

    public void setLogicalBlockAddress(int logicalBlockAddress) {
        this.mLogicalBlockAddress = logicalBlockAddress;
    }

    public void setControl(byte control) {
        this.mControl = control;
    }
}
