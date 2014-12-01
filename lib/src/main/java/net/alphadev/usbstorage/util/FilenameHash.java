package net.alphadev.usbstorage.util;

import net.alphadev.usbstorage.api.Path;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class FilenameHash {
    public static final String getHash(Path path) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] hash = md.digest(path.toAbsolute().getBytes());
        return byteArrayToHexString(hash);
    }

    private static String byteArrayToHexString(byte[] b) {
        String result = "";
        for (byte element : b) {
            int value = (element & 0xff) + 0x100;
            result += Integer.toString(value, 16).substring(1);
        }
        return result;
    }
}
