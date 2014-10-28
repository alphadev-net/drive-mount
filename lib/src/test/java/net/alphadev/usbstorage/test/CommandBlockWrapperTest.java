package net.alphadev.usbstorage.test;

import net.alphadev.usbstorage.api.Transmittable;
import net.alphadev.usbstorage.bbb.CommandBlockWrapper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class CommandBlockWrapperTest {
    private CommandBlockWrapper cbw;

    @Before
    public void init() {
        cbw = new CommandBlockWrapper();
        cbw.setFlags(CommandBlockWrapper.Direction.HOST_TO_DEVICE);
        cbw.setLun((byte) 0);
        cbw.setCommand(new TransmittableDummy(new byte[]{1, 2, 3, 4, 5, 6}));
    }

    @Test
    public void checkAgainstValidCBW() {
        byte[] expected = new byte[]{
                0x55, 0x53, 0x42, 0x43, // signature
                0x0, 0x0, 0x0, 0x1, // tag
                0x24, 0x0, 0x0, 0x0, // transfer length
                0x0, // flags
                0x0, // lun
                0x6, // length
                0x1, 0x2, 0x3, 0x4, // payload 1
                0x5, 0x6, 0x0, 0x0, // payload 2
                0x0, 0x0, 0x0, 0x0, // payload 3
                0x0, 0x0, 0x0, 0x0  // payload 4
        };
        Assert.assertArrayEquals(expected, cbw.asBytes());
    }

    @Test
    public void checkPayloadSizeCalculation() {
        byte[] cbwBytes = cbw.asBytes();
        byte length = cbwBytes[0xe];

        int counter = 0;
        int lastPos = 0;
        for (int i = 0xf; i < cbwBytes.length; i++) {
            if (cbwBytes[i] != 0) {
                counter++;
                lastPos = i;
            }
        }

        Assert.assertEquals(length, counter);
        Assert.assertEquals(15 + length - 1, lastPos);
    }

    private static class TransmittableDummy implements Transmittable {
        private byte[] data;

        private TransmittableDummy(byte[] data) {
            this.data = data;
        }

        @Override
        public byte[] asBytes() {
            return data;
        }

        @Override
        public int getExpectedAnswerLength() {
            return 36;
        }
    }
}
