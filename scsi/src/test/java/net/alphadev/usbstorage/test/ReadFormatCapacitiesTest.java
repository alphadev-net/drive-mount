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
package net.alphadev.usbstorage.test;

import net.alphadev.usbstorage.scsi.answer.ReadFormatCapacitiesHeader;

import org.junit.Test;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class ReadFormatCapacitiesTest {
    @Test
    public void doesNotComplainOnValidValues() {
        new ReadFormatCapacitiesHeader(new byte[]{
                0, 0, 0, 8, 7, 0x33, (byte) 0xf3, (byte) 0xf4, 2, 0, 2, 0
        });
        new ReadFormatCapacitiesHeader(new byte[]{
                0, 0, 0, 0x10, 7, 0x33, (byte) 0xf3, (byte) 0xf4, 2, 0, 2, 0
        });
        new ReadFormatCapacitiesHeader(new byte[]{
                0, 0, 0, 0x18, 7, 0x33, (byte) 0xf3, (byte) 0xf4, 2, 0, 2, 0
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void doesComplainonInvalidValuesLowerBound() {
        byte[] data = new byte[]{
                0, 0, 0, 0, 7, 0x33, (byte) 0xf3, (byte) 0xf4, 2, 0, 2, 0
        };

        new ReadFormatCapacitiesHeader(data);
    }

    @Test(expected = IllegalArgumentException.class)
    public void doesComplainonInvalidValuesUpperBound() {
        byte[] data = new byte[]{
                0, 0, 0, (byte) 0xff, 7, 0x33, (byte) 0xf3, (byte) 0xf4, 2, 0, 2, 0
        };

        new ReadFormatCapacitiesHeader(data);
    }

    @Test(expected = IllegalArgumentException.class)
    public void doesComplainonInvalidValuesInvalid() {
        byte[] data = new byte[]{
                0, 0, 0, 5, 7, 0x33, (byte) 0xf3, (byte) 0xf4, 2, 0, 2, 0
        };

        new ReadFormatCapacitiesHeader(data);
    }
}
