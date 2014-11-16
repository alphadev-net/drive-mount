package net.alphadev.usbstorage.util;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
@SuppressWarnings("unused")
public class HashCodeUtil {
    public static int getHashCode(Object... objects) {
        int hash = 23;
        for (Object object : objects) {
            hash = hash * 31 + object.hashCode();
        }
        return hash;
    }
}
