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
package net.alphadev.usbstorage.scsi;

import net.alphadev.usbstorage.api.scsi.ScsiTransferable;
import net.alphadev.usbstorage.util.BitStitching;

import java.nio.ByteOrder;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class CommandBlockWrapper implements ScsiTransferable {
    private static int tagCounter = 0;
    private final byte[] cwbData;

    public CommandBlockWrapper() {
        cwbData = new byte[0x1f];

        // set CBW signature
        cwbData[0x0] = 'U';
        cwbData[0x1] = 'S';
        cwbData[0x2] = 'B';
        cwbData[0x3] = 'C';

        // increase and write tag counter
        BitStitching.setBytesFromInt(++tagCounter, cwbData, 0x4, ByteOrder.LITTLE_ENDIAN);
    }

    public void setFlags(Direction directionFlags) {
        cwbData[0xc] = (byte) (directionFlags == Direction.DEVICE_TO_HOST ? 128 : 0);
    }

    public void setLun(byte lun) {
        cwbData[0xd] = lun;
    }

    public void setCommand(ScsiTransferable command) {
        byte[] cmdBlock = command.asBytes();

        if (cmdBlock.length != 6 && cmdBlock.length != 10 &&
                cmdBlock.length != 12 && cmdBlock.length != 16) {
            throw new IllegalArgumentException("command has invalid size!");
        }

        int cmdOffset = 0xf;
        System.arraycopy(cmdBlock, 0, cwbData, cmdOffset, cmdBlock.length);

        cwbData[0xe] = (byte) cmdBlock.length;
        BitStitching.setBytesFromInt(command.getExpectedAnswerLength(), cwbData, 0x8, ByteOrder.LITTLE_ENDIAN);
    }

    public byte[] asBytes() {
        return cwbData.clone();
    }

    @Override
    public int getExpectedAnswerLength() {
        return 0;
    }

    public enum Direction {
        HOST_TO_DEVICE,
        DEVICE_TO_HOST
    }
}
