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
