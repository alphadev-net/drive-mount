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

import net.alphadev.usbstorage.scsi.answer.StandardInquiryAnswer;
import net.alphadev.usbstorage.util.BitStitching;

import org.junit.Test;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class CrashTests {
    @Test
    public void testIssue7() {
        byte[] data = BitStitching.forceCast(new int[]{ // WD My Passport SIA
                0x00, 0x00, 0x06, 0x12, 0x5b, 0x00, 0x00, 0x00, 0x57, 0x44, 0x20, 0x20, 0x20, 0x20,
                0x20, 0x20, 0x4d, 0x79, 0x20, 0x50, 0x61, 0x73, 0x73, 0x70, 0x6f, 0x72, 0x74, 0x20,
                0x30, 0x38, 0x33, 0x30, 0x31, 0x30, 0x35, 0x36
        });

        new StandardInquiryAnswer(data);
    }

    @Test
    public void testIssue14() {
        byte[] data = BitStitching.forceCast(new int[]{
                0X00, 0x80, 0x04, 0x02, 0x1f, 0x00, 0x00, 0x00, 0x4b, 0x69, 0x6e, 0x67, 0x73, 0x74,
                0x6f, 0x6e, 0x44, 0x61, 0x74, 0x61, 0x54, 0x72, 0x61, 0x76, 0x65, 0x6c, 0x65, 0x72,
                0x20, 0x32, 0x2e, 0x30, 0x31, 0x2e, 0x30, 0x30
        });
        new StandardInquiryAnswer(data);
    }
}
