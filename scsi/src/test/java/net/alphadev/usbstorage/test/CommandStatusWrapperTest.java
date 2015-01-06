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

import net.alphadev.usbstorage.scsi.CommandStatusWrapper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class CommandStatusWrapperTest {
    private CommandStatusWrapper csw;

    @Before
    public void init() {
        byte[] statusData = new byte[]{
                0x55, 0x53, 0x42, 0x53, // signature
                0x1, 0x0, 0x0, 0x0, // tag
                0x0, 0x0, 0x0, 0x0, // data residue
                0x0 // status flag
        };
        csw = new CommandStatusWrapper(statusData);
    }

    @Test
    public void testSignature() {
        Assert.assertEquals("USBS", csw.getSignature());
    }

    @Test
    public void testTag() {
        Assert.assertEquals(1, csw.getTag());
    }

    @Test
    public void testStatus() {
        Assert.assertEquals(CommandStatusWrapper.Status.COMMAND_PASSED, csw.getStatus());
    }

    @Test
    public void testDataResidue() {
        Assert.assertEquals(0, csw.getDataResidue());
    }
}
