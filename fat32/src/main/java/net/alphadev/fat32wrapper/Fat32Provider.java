/**
 * Copyright © 2014 Jan Seeger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.alphadev.fat32wrapper;

import net.alphadev.usbstorage.api.FileAttribute;
import net.alphadev.usbstorage.api.FileHandle;
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
    public Object getAttribute(Path path, FileAttribute attr) {
        switch (attr) {
            case FILESIZE:
                return getFileSize(path);
            case LAST_MODIFIED:
                return getLastModified(path);
            default:
                return null;
        }
    }

    private long getFileSize(Path path) {
        FatFile file = getFileOrNull(path);
        return file != null ? file.getLength() : 0;
    }

    private long getLastModified(Path path) {
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

    @Override
    public FileHandle openDocument(Path path) {
        System.out.println("Opening " + path.toAbsolute() + " for reading");
        final FatFile fatFile = getFileOrNull(path);
        return new ReadingFileHandle(fatFile);
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
                return entry.getDirectory();
            } catch (IOException e) {
                // don't care just return null
            }
        }

        return null;
    }
}
