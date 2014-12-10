package net.alphadev.usbstorage.util;

import net.alphadev.usbstorage.api.Path;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public final class FilenameHash {
    public static String getHash(Path path) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Could not find Algorithm", e);
        }

        String absolutePath = path.toAbsolute();
        if(absolutePath == null) {
            absolutePath = "";
        }

        final byte[] hash = md.digest(absolutePath.getBytes());
        return byteArrayToHexString(hash);
    }

    private static String byteArrayToHexString(byte[] b) {
        String result = "";
        for (byte element : b) {
            final int value = (element & 0xff) + 0x100;
            result += Integer.toString(value, 16).substring(1);
        }
        return result;
    }
}
