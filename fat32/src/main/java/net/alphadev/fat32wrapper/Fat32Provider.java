/**
 * Copyright © 2014-2015 Jan Seeger
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

import net.alphadev.usbstorage.api.device.BlockDevice;
import net.alphadev.usbstorage.api.filesystem.FileAttribute;
import net.alphadev.usbstorage.api.filesystem.FileHandle;
import net.alphadev.usbstorage.api.filesystem.FileSystemProvider;
import net.alphadev.usbstorage.api.filesystem.Path;

import java.util.Collections;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class Fat32Provider implements FileSystemProvider {
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final BlockDevice fs;

    public Fat32Provider(BlockDevice fs) {
        this.fs = fs;
    }

    @Override
    public boolean isDirectory(Path path) {
        return false;
    }

    @Override
    public Iterable<Path> getEntries(Path path) {
        return Collections.emptySet();
    }

    @Override
    public Object getAttribute(Path path, FileAttribute attr) {
        switch (attr) {
//            case FILESIZE:
//                return getFileSize(path);
//            case LAST_MODIFIED:
//                return getLastModified(path);
            default:
                return null;
        }
    }

    @Override
    public FileHandle openDocument(Path path) {
        return null;
    }
}
