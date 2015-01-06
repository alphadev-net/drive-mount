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

import net.alphadev.usbstorage.scsi.CommandBlockWrapper;
import net.alphadev.usbstorage.util.BitStitching;

import java.nio.ByteOrder;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class Read10 extends ScsiCommand {
    private static final byte READ10 = 0x28;

    private long offset;
    private short transferLength;
    private int mAnswerLength;

    @Override
    public CommandBlockWrapper.Direction getDirection() {
        return CommandBlockWrapper.Direction.DEVICE_TO_HOST;
    }

    @Override
    public byte[] asBytes() {
        final byte[] bytes = new byte[10];
        bytes[0] = READ10; // opcode
        // 1 == flags
        BitStitching.setBytesFromInt((int) offset, bytes, 2, ByteOrder.BIG_ENDIAN);
        // 6 == group number
        BitStitching.setBytesFromShort(transferLength, bytes, 7, ByteOrder.BIG_ENDIAN);
        // 9 == control bits
        return bytes;
    }

    @Override
    public int getExpectedAnswerLength() {
        return mAnswerLength;
    }

    public void setExpectedAnswerLength(int length) {
        mAnswerLength = length;
    }

    /**
     * Sets read offset in logical blocks.
     *
     * @param offset as absolute value
     */
    public void setOffset(long offset) {
        this.offset = offset;
    }

    public void setTransferLength(short transferLength) {
        this.transferLength = transferLength;
    }
}
