package net.alphadev.usbstorage.api;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public interface FileSystemProvider {
    boolean isDirectory(Path path);

    Iterable<Path> getEntries(Path path);

    long getFileSize(Path path);

    long getLastModified(Path path);
}
