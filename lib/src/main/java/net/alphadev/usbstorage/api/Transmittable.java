package net.alphadev.usbstorage.api;

/**
 *
 * @author Jan Seeger <jan@alphadev.net>
 */
public interface Transmittable {
    byte[] asBytes();

    int getExpectedAnswerLength();
}
