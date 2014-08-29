package net.alphadev.usbstorage.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by jan on 28.08.14.
 */
public class BitStitching {
    public static int convertToInt(byte[] byteArray, int offset) {
        byte c1 = byteArray[offset + 3];
        byte c2 = byteArray[offset + 2];
        byte c3 = byteArray[offset + 1];
        byte c4 = byteArray[offset];

        long temp =
                ((0xFF & c1) << 24) | ((0xFF & c2) << 16) | ((0xFF & c3) << 8) | (0xFF & c4);

        return (int) (temp & 0x0FFFFFFFFL);
    }

    public static void setBytesFromInt(int integer, byte[] array, int offset, int length) {
        ByteBuffer b = ByteBuffer.allocate(4);
        b.order(ByteOrder.LITTLE_ENDIAN);
        b.putInt(integer);
        byte[] temp = b.array();

        for (int i = 0; i < temp.length; i++) {
            int index = offset + i;
            array[index] = temp[i];
        }
    }

    public static String bytesToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a)
            sb.append(String.format("%02x ", b & 0xff));
        return sb.toString();
    }

    public static short convertToShort(byte[] byteArray, int offset) {
        byte c1 = byteArray[offset + 1];
        byte c2 = byteArray[offset];
        long temp = ((0xFF & c1) << 8) | (0xFF & c2);
        return (short) (temp & 0x0FFFFFFFFL);
    }

    public static String bytesToString(byte[] answer, int offset, int length) {
        ByteBuffer buffer = ByteBuffer.allocate(length);
        buffer.put(answer, offset, length);
        return new String(buffer.array());
    }
}
