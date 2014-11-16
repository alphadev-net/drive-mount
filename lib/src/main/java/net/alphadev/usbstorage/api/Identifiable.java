package net.alphadev.usbstorage.api;

/**
 * This interface enforces the presence of an ID accessor.
 *
 * @author Jan Seeger <jan@alphadev.net>
 */
public interface Identifiable {
    /**
     * @return Identifier
     */
    int getId();
}
