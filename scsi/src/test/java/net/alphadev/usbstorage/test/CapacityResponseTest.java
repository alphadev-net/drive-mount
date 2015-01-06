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

import net.alphadev.usbstorage.scsi.answer.ReadCapacityResponse;
import net.alphadev.usbstorage.util.BitStitching;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class CapacityResponseTest {

    @Test
    public void doesNotComplainOnValidValues() {
        byte[] data = BitStitching.forceCast(new int[]{
                0x07, 0x33, 0xf3, 0xf3, 0x00, 0x00, 0x02, 0x00
        });
        final ReadCapacityResponse capacity = new ReadCapacityResponse(data);

        Assert.assertEquals(120845299, capacity.getNumberOfBlocks());
        Assert.assertEquals(512, capacity.getBlockSize());
    }
}
