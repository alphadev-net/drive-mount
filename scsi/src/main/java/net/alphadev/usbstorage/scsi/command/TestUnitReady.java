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

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class TestUnitReady extends ScsiCommand {
    @Override
    public byte[] asBytes() {
        // all zero since even opcode == 0x0
        return new byte[6];
    }

    @Override
    public int getExpectedAnswerLength() {
        return 0;
    }
}
