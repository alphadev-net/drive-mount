package net.alphadev.usbstorage.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
@SuppressWarnings("unused")

public class BitStitching {
    public static int convertToInt(byte[] byteArray, int offset, ByteOrder order) {
        ByteBuffer b = ByteBuffer.wrap(byteArray, offset, 4);
        b.order(order);
        return b.getInt();
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

    public static short convertToShort(byte[] byteArray, int offset, ByteOrder order) {
        ByteBuffer b = ByteBuffer.wrap(byteArray, offset, 2);
        b.order(order);
        return b.getShort();
    }

    /**
     * Used to read the Vendor and Model descriptions.
     */
    public static String bytesToString(byte[] answer, int offset, int length) {
        ByteBuffer buffer = ByteBuffer.allocate(length);
        buffer.put(answer, offset, length);
        return new String(buffer.array());
    }

    public static void setBytesFromShort(short value, byte[] array, int offset, ByteOrder order) {
        ByteBuffer b = ByteBuffer.allocate(2);
        b.order(order);
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

    public static byte[] forceCast(int[] input) {
        byte[] output = new byte[input.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = (byte) input[i];
        }
        return output;
    }
}
