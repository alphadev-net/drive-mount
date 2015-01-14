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

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import de.waldheinz.fs.FsFile;

public class ReadingFileHandle extends InputStream {
    private final int totalFileSize;
    private FsFile file;
    private int position;

    public ReadingFileHandle(FsFile file) {
        this.file = file;
        totalFileSize = (file != null) ? (int) file.getLength() : 0;
    }

    @Override
    public int read(@NotNull byte[] buffer) throws IOException {
        final int bytesRemaining = available();

        if (bytesRemaining <= 0) {
            return -1;
        }

        final int bytesRead = Math.min(buffer.length, bytesRemaining);
        final ByteBuffer bb = ByteBuffer.wrap(buffer);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.limit(bytesRead);

        if (file != null) {
            file.read(position, bb);
            position += bytesRead;
        }

        return bytesRead;
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

        if (file != null) {
            file.read(position, bb);
            position += 4;
        }

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
