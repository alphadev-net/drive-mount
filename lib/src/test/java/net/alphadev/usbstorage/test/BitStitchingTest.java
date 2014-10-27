package net.alphadev.usbstorage.test;

import net.alphadev.usbstorage.util.BitStitching;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class BitStitchingTest {
    @Test
    public void convertsProperlyToInteger() {
        testNumber(new byte[]{0,0,0,0}, 0);
        testNumber(new byte[]{0,0,2,0}, 512);
    }

    private void testNumber(byte[] input, int expected) {
        int result = BitStitching.convertToInt(input, 0);
        Assert.assertEquals(expected, result);
    }
}
