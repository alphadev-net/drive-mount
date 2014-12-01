package net.alphadev.usbstorage.api;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public interface FileSystemProvider {
    boolean isDirectory(Path path);

    Iterable<Path> getEntries(Path path);
}
