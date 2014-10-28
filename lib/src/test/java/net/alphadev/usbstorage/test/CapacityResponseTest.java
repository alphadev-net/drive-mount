package net.alphadev.usbstorage.test;

import net.alphadev.usbstorage.scsi.answer.ReadCapacityResponse;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class CapacityResponseTest {

    @Test
    public void doesNotComplainOnValidValues() {
        final ReadCapacityResponse capacity = new ReadCapacityResponse(new byte[]{
                7, 0x33, (byte) 0xf3, (byte) 0xf3, 0, 0, 2, 0
        });

        Assert.assertEquals(120845299, capacity.getNumberOfBlocks());
        Assert.assertEquals(512, capacity.getBlockSize());
    }
}
