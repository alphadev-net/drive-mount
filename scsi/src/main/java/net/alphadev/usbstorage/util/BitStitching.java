/**
 * Copyright © 2014 Jan Seeger
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
package net.alphadev.usbstorage.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
@SuppressWarnings("unused")
public class BitStitching {
    public static int convertToInt(byte[] byteArray, int offset, ByteOrder order) {
        ByteBuffer b = ByteBuffer.allocate(4);
        b.order(order);
        b.put(byteArray, offset, 4);
        b.rewind();
        return b.getInt();
    }

    public static void setBytesFromInt(int integer, byte[] array, int offset, ByteOrder order) {
        ByteBuffer b = ByteBuffer.allocate(4);
        b.order(order);
        b.putInt(integer);
        byte[] temp = b.array();

        setBytes(temp, array, offset, 4);
    }

    public static String convertBytesToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a)
            sb.append(String.format("%02x ", b & 0xff));
        return sb.toString();
    }

    public static String convertByteBufferToHex(final ByteBuffer buffer) {
        final byte[] bytes = new byte[buffer.remaining()];
        buffer.duplicate().get(bytes);
        return convertBytesToHex(bytes);
    }

    public static short convertToShort(byte[] byteArray, int offset, ByteOrder order) {
        ByteBuffer b = ByteBuffer.allocate(2);
        b.order(order);
        b.put(byteArray, offset, 2);
        b.rewind();
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
