package net.alphadev.usbstorage.api;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public interface ScsiTransferable extends Transmittable {
    int getExpectedAnswerLength();
}
