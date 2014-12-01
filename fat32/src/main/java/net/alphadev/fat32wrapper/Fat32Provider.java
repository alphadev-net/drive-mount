package net.alphadev.fat32wrapper;

import net.alphadev.usbstorage.api.FileSystemProvider;
import net.alphadev.usbstorage.api.Path;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.waldheinz.fs.FsDirectoryEntry;
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

        FatLfnDirectory directory = null;
        if (path.isRoot()) {
            directory = fs.getRoot();
        } else {
            final FatLfnDirectoryEntry dirEntry = getEntry(path);
            if (dirEntry.isDirectory()) {
                try {
                    directory = dirEntry.getDirectory();
                } catch (IOException e) {
                    // we have just checked if it is a directory!
                }
            }
        }

        if (directory != null) {
            for (FsDirectoryEntry entry : directory) {
                entries.add(Path.createWithAppended(path, entry.getName()));
            }
        }

        return entries;
    }

    private FatLfnDirectoryEntry getEntry(Path path) {
        FatLfnDirectory lastDir = fs.getRoot();
        FatLfnDirectoryEntry lastEntry = null;

        for (String segment : path.getIterator()) {
            lastEntry = lastDir.getEntry(segment);
            if (lastEntry.isDirectory()) {
                try {
                    lastDir = lastEntry.getDirectory();
                } catch (IOException e) {
                    // we have just checked if it is a directory!
                }
            }
        }

        return lastEntry;
    }
}
