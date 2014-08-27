package net.alphadev.usbstorage.test;

import org.junit.Assert;

import net.alphadev.usbstorage.scsi.CommandStatusWrapper;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by jan on 27.08.14.
 */
public class CommandStatusWrapperTest {
    private CommandStatusWrapper csw;

    @Before
    public void init() {
        byte[] statusData = new byte[]{
            0x55, 0x53, 0x42, 0x53, // signature
             0x1,  0x0,  0x0,  0x0, // tag
             0x0,  0x0,  0x0,  0x0, // data residue
             0x0 // status flag
        };
        csw = new CommandStatusWrapper(statusData);
    }

    @Test
    public void testSignature() {
        String result = csw.getSignature();
        Assert.assertEquals("USBS", result);
    }

    @Test
    public void testTag() {
        int result = csw.getTag();
        Assert.assertEquals(1, result);
    }
}
