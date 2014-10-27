package net.alphadev.usbstorage.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author Jan Seeger <jan@alphadev.net>
 */
@SuppressWarnings("unused")
public class BitStitching {
    public static int convertToInt(byte[] byteArray, int offset) {
        byte c1 = byteArray[offset];
        byte c2 = byteArray[offset + 1];
        byte c3 = byteArray[offset + 2];
        byte c4 = byteArray[offset + 3];

        long temp =
                ((0xFF & c1) << 24) | ((0xFF & c2) << 16) | ((0xFF & c3) << 8) | (0xFF & c4);

        return (int) (temp & 0x0FFFFFFFFL);
    }

    public static void setBytesFromInt(int integer, byte[] array, int offset) {
        setBytesFromInt(integer, array, offset, ByteOrder.LITTLE_ENDIAN);
    }

    public static void setBytesFromInt(int integer, byte[] array, int offset, ByteOrder order) {
        ByteBuffer b = ByteBuffer.allocate(4);
        b.order(order);
        b.putInt(integer);
        byte[] temp = b.array();

        setBytes(temp, array, offset, 4);
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

    public static void setBytesFromShort(short value, byte[] array, int offset) {
        ByteBuffer b = ByteBuffer.allocate(2);
        b.order(ByteOrder.LITTLE_ENDIAN);
        b.putShort(value);
        byte[] temp = b.array();

        setBytes(temp, array, offset, 2);
    }

    private static void setBytes(byte[] a, byte[] array, int offset, int length) {
        for (int i = 0; i < length; i++) {
            int index = offset + i;
            array[index] = a[i];
        }
    }
}
