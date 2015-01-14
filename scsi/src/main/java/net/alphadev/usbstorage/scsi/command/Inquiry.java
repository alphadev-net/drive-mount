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

import net.alphadev.usbstorage.scsi.answer.StandardInquiryAnswer;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
@SuppressWarnings("unused")
public class Inquiry extends ScsiCommand {
    public static final byte INQUIRY = 0x12;

    private boolean mCmdDt;
    private boolean mEvpd;

    @Override
    public byte[] asBytes() {
        byte[] buffer = new byte[6];
        buffer[0] = INQUIRY; // opcode
        if (mCmdDt) {
            buffer[1] += 2;
        }
        if (mEvpd) {
            buffer[1] += 1;
        }
        buffer[4] = StandardInquiryAnswer.LENGTH;    // LENGTH
        return buffer;
    }

    @Override
    public int getExpectedAnswerLength() {
        return StandardInquiryAnswer.LENGTH;
    }

    public boolean isCmdDt() {
        return mCmdDt;
    }

    public boolean isEvpd() {
        return mEvpd;
    }
}
