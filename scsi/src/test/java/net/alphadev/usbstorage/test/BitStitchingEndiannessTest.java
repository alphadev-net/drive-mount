/**
 * Copyright © 2014 Jan Seeger
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
package net.alphadev.usbstorage.test;

import net.alphadev.usbstorage.util.BitStitching;

import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteOrder;

/**
 * Test correct handling of endianness in BitStitching util class.
 *
 * @author Jan Seeger <jan@alphadev.net>
 * @see <a href="http://de.wikipedia.org/wiki/Byte-Reihenfolge#Beispiel:_Speicherung_einer_Integer-Zahl_von_32_Bit_in_4_Bytes">http://de.wikipedia.org/wiki/Byte-Reihenfolge#Beispiel:_Speicherung_einer_Integer-Zahl_von_32_Bit_in_4_Bytes</a>
 */
public class BitStitchingEndiannessTest {
    /**
     * Convert little endian byte[] to big endian number (java default).
     */
    @Test
    public void convertsProperlyToLittleEndianByteArray() {
        byte[] expected = new byte[]{0x4d, 0x3c, 0x2b, 0x1a};
        int input = 439041101;
        byte[] result = new byte[4];
        BitStitching.setBytesFromInt(input, result, 0, ByteOrder.LITTLE_ENDIAN);
        Assert.assertArrayEquals(expected, result);
    }

    @Test
    public void convertsProperlyToInteger() {
        int expected = 512;
        byte[] input = new byte[]{0, 2, 0, 0};
        int result = BitStitching.convertToInt(input, 0, ByteOrder.LITTLE_ENDIAN);
        Assert.assertEquals(expected, result);
    }
}
