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

import net.alphadev.usbstorage.api.FileHandle;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import de.waldheinz.fs.fat.FatFile;

public class ReadingFileHandle implements FileHandle {
    private final FatFile file;

    public ReadingFileHandle(FatFile file) {
        this.file = file;
    }

    @Override
    public InputStream readDocument() {
        return new BlaStream(file);
    }

    private static class BlaStream extends InputStream {
        private FatFile file;
        private int totalFileSize;
        private int position;

        public BlaStream(FatFile file) {
            if (file != null) {
                totalFileSize = (int) file.getLength();
            }
        }

        @Override
        public int read(@NotNull byte[] buffer) throws IOException {
            final int bytesRemaining = available();
            final int bytesRead = Math.min(buffer.length, bytesRemaining);
            final boolean shouldCallAgain = (bytesRemaining - bytesRead > 0);
            final ByteBuffer bb = ByteBuffer.wrap(buffer);
            bb.order(ByteOrder.LITTLE_ENDIAN);

            if (file != null) {
                file.read(position, bb);
                position += bytesRead;
            }

            return shouldCallAgain ? bytesRead : -1;
        }

        @Override
        public int available() {
            return totalFileSize - position;
        }

        @Override
        public int read() throws IOException {
            if (available() <= 0) {
                return -1;
            }

            final byte[] buffer = new byte[4];
            final ByteBuffer bb = ByteBuffer.wrap(buffer);
            bb.order(ByteOrder.LITTLE_ENDIAN);

            file.read(position, bb);
            position += 4;

            return bb.getInt(position);
        }

        @Override
        public boolean markSupported() {
            return false;
        }

        @Override
        public void close() {
            this.file = null;
        }
    }
}
