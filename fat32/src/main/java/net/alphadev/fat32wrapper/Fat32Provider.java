package net.alphadev.fat32wrapper;

import net.alphadev.usbstorage.api.FileSystemProvider;
import net.alphadev.usbstorage.api.Path;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.waldheinz.fs.FsDirectoryEntry;
import de.waldheinz.fs.fat.FatFile;
import de.waldheinz.fs.fat.FatFileSystem;
import de.waldheinz.fs.fat.FatLfnDirectory;
import de.waldheinz.fs.fat.FatLfnDirectoryEntry;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class Fat32Provider implements FileSystemProvider {
    private final FatFileSystem fs;

    public Fat32Provider(FatFileSystem fs) {
        this.fs = fs;
    }

    @Override
    public boolean isDirectory(Path path) {
        FatLfnDirectoryEntry file = getEntry(path);
        return file != null && file.isDirectory();
    }

    @Override
    public Iterable<Path> getEntries(Path path) {
        final List<Path> entries = new ArrayList<>();

        FatLfnDirectory directory;
        if (path.isRoot()) {
            directory = fs.getRoot();
        } else {
            final FatLfnDirectoryEntry dirEntry = getEntry(path);
            directory = getDirectoryOrNull(dirEntry);
        }

        if (directory != null) {
            for (FsDirectoryEntry entry : directory) {
                entries.add(Path.createWithAppended(path, entry.getName()));
            }
        }

        return entries;
    }

    @Override
    public long getFileSize(Path path) {
        FatFile file = getFileOrNull(path);
        return file != null ? file.getLength() : 0;
    }

    @Override
    public long getLastModified(Path path) {
        FatLfnDirectoryEntry entry = getEntry(path);
        if (entry != null && entry.isFile()) {
            try {
                return entry.getLastModified();
            } catch (IOException e) {
                return 0;
            }
        }

        return 0;
    }

    private FatLfnDirectoryEntry getEntry(Path path) {
        FatLfnDirectory lastDir = fs.getRoot();
        FatLfnDirectoryEntry lastEntry = null;

        for (String segment : path.getIterator()) {
            if (lastDir != null) {
                lastEntry = lastDir.getEntry(segment);
                lastDir = getDirectoryOrNull(lastEntry);
            }
        }

        return lastEntry;
    }

    private FatFile getFileOrNull(Path path) {
        FatLfnDirectoryEntry entry = getEntry(path);
        if (entry != null && entry.isFile()) {
            try {
                return entry.getFile();
            } catch (IOException e) {
                // yeah, we already checked!
            }
        }
        return null;
    }

    private FatLfnDirectory getDirectoryOrNull(FatLfnDirectoryEntry entry) {
        if (entry.isDirectory()) {
            try {
                entry.getDirectory();
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }
}
