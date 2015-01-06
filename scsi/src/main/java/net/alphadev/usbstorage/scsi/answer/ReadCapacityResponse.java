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
public class ReadCapacityResponse {
    public static final int LENGTH = 8;

    private final int mBlockSize;
    private final int mNumberOfBlocks;

    public ReadCapacityResponse(byte[] answer) {
        mNumberOfBlocks = BitStitching.convertToInt(answer, 0, ByteOrder.BIG_ENDIAN);
        mBlockSize = BitStitching.convertToInt(answer, 4, ByteOrder.BIG_ENDIAN);
    }

    public int getBlockSize() {
        return mBlockSize;
    }

    public int getNumberOfBlocks() {
        return mNumberOfBlocks;
    }
}
