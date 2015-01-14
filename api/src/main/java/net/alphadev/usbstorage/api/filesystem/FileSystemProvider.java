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
package net.alphadev.usbstorage.api.filesystem;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public interface FileSystemProvider {
    /**
     * Returs true if, and only if, the item represented by the given Path is a directory.
     *
     * @param path Path to check
     * @return true if is directory
     */
    boolean isDirectory(Path path);

    /**
     * Returns list of Paths (sub entries) for a given Path or an empty list otherwise.
     *
     * @param path path Path to look for sub entries
     * @return List of Paths
     */
    Iterable<Path> getEntries(Path path);

    /**
     * Returns the requested File Attribute or null if not applicable.
     *
     * @param path Path to get the Attribute for
     * @param attr Type of Attribute accortidng to FileAttribute
     * @return Attribute value or null
     */
    Object getAttribute(Path path, FileAttribute attr);

    FileHandle openDocument(Path path);
}
