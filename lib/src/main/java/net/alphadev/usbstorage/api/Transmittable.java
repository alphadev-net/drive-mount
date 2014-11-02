package net.alphadev.usbstorage.api;

/**
 * Represents a payload that is transmitted between an USB device and its driver.
 *
 * @author Jan Seeger <jan@alphadev.net>
 */
public interface Transmittable {
    /**
     * Returns the payload data as byte array.
     *
     * @return payload data
     */
    byte[] asBytes();
}
