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
package net.alphadev.usbstorage;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.DocumentsContract.Document;
import android.provider.DocumentsContract.Root;
import android.provider.DocumentsProvider;
import android.util.Log;

import net.alphadev.usbstorage.api.FileAttribute;
import net.alphadev.usbstorage.api.FileHandle;
import net.alphadev.usbstorage.api.FileSystemProvider;
import net.alphadev.usbstorage.api.Path;
import net.alphadev.usbstorage.api.StorageDevice;
import net.alphadev.usbstorage.util.FilenameHash;
import net.alphadev.usbstorage.util.MimeUtil;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

public class DocumentProviderImpl extends DocumentsProvider {

    private static final String AUTHORITY = "net.alphadev.usbstorage.documents";

    private static final String[] DEFAULT_ROOT_PROJECTION = new String[]{
            Root.COLUMN_ROOT_ID, Root.COLUMN_FLAGS, Root.COLUMN_ICON, Root.COLUMN_TITLE,
            Root.COLUMN_DOCUMENT_ID, Root.COLUMN_AVAILABLE_BYTES, Root.COLUMN_SUMMARY
    };
    private static final String[] DEFAULT_DOCUMENT_PROJECTION = new String[]{
            Document.COLUMN_DOCUMENT_ID, Document.COLUMN_MIME_TYPE, Document.COLUMN_DISPLAY_NAME,
            Document.COLUMN_LAST_MODIFIED, Document.COLUMN_FLAGS, Document.COLUMN_SIZE,
    };
    private StorageManager mStorageManager;

    private static String[] resolveRootProjection(String[] projection) {
        return projection != null ? projection : DEFAULT_ROOT_PROJECTION;
    }

    private static String[] resolveDocumentProjection(String[] projection) {
        return projection != null ? projection : DEFAULT_DOCUMENT_PROJECTION;
    }

    /**
     * http://stackoverflow.com/questions/3263892/format-file-size-as-mb-gb-etc
     */
    private static String readableFileSize(long size) {
        if (size <= 0) return "0B";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        float roundedSize = (float) (size / Math.pow(1024, digitGroups));

        return new DecimalFormat("#,##0.#").format(roundedSize) + " " + units[digitGroups];
    }

    @Override
    public boolean onCreate() {
        mStorageManager = new StorageManager();
        final DeviceManager deviceManager = new DeviceManager(getContext(), mStorageManager);
        deviceManager.setOnStorageChangedListener(new OnStorageChangedListener() {
            @Override
            public void onStorageChange() {
                getContext().getContentResolver()
                        .notifyChange(DocumentsContract
                                .buildRootsUri(AUTHORITY), null);
            }
        });

        return true;
    }

    @Override
    public Cursor queryRoots(final String[] requestedProjection) throws FileNotFoundException {
        final String[] projection = resolveRootProjection(requestedProjection);
        final MatrixCursor roots = new MatrixCursor(projection);

        for (StorageDevice device : mStorageManager.getMounts()) {
            createDevice(roots.newRow(), device, projection);
        }

        return roots;
    }

    private void createDevice(MatrixCursor.RowBuilder row, StorageDevice device,
                              String[] projection) {
        for (String column : projection) {
            switch (column) {
                case Root.COLUMN_ROOT_ID:
                    row.add(Root.COLUMN_ROOT_ID, device.getId());
                    break;
                case Root.COLUMN_DOCUMENT_ID:
                    row.add(Root.COLUMN_DOCUMENT_ID, device.getId());
                    break;
                case Root.COLUMN_TITLE:
                    row.add(Root.COLUMN_TITLE, device.getName());
                    break;
                case Root.COLUMN_ICON:
                    row.add(Root.COLUMN_ICON, R.drawable.drive_icon_gen);
                    break;
                case Root.COLUMN_SUMMARY:
                    final String sizeUnit = readableFileSize(device.getUnallocatedSpace());
                    final String summary = getContext().getString(R.string.free_space, sizeUnit);
                    row.add(Root.COLUMN_SUMMARY, summary);
                    break;
                case Root.COLUMN_AVAILABLE_BYTES:
                    row.add(Root.COLUMN_AVAILABLE_BYTES, device.getUnallocatedSpace());
                    break;
                case Root.COLUMN_FLAGS:
                    int flags = 0;
                    if (!device.isReadOnly()) {
                        flags |= Root.FLAG_SUPPORTS_CREATE;
                    }
                    row.add(Root.COLUMN_FLAGS, flags);
                    break;
                default:
                    Log.w("Drive Mount", "Couldn't satisfy " + column + " column.");
            }
        }
    }

    @Override
    public Cursor queryDocument(String documentId,
                                final String[] requestedProjection) throws FileNotFoundException {
        final String[] projection = resolveDocumentProjection(requestedProjection);
        final MatrixCursor result = new MatrixCursor(projection);

        addEntry(result, new Path(documentId), projection);

        return result;
    }

    @Override
    public Cursor queryChildDocuments(String parentDocumentId, final String[] requestedProjection,
                                      String sortOrder) throws FileNotFoundException {
        final String[] projection = resolveDocumentProjection(requestedProjection);
        final MatrixCursor result = new MatrixCursor(projection);
        final Path parent = new Path(parentDocumentId);
        final FileSystemProvider provider = getProvider(parent);

        for (Path child : provider.getEntries(parent)) {
            addEntry(result, child, projection);
        }

        return result;
    }

    @Override
    public ParcelFileDescriptor openDocument(final String documentId, final String mode,
                                             CancellationSignal signal) throws FileNotFoundException {
        try {
            final Path path = new Path(documentId);
            final File tempFile = getTemporaryName(path);
            writeToCacheFile(path, tempFile);

            return ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void addEntry(MatrixCursor cursor, Path path, String[] projection) {
        final MatrixCursor.RowBuilder row = cursor.newRow();
        final FileSystemProvider provider = getProvider(path);

        for (String column : projection) {
            switch (column) {
                case Document.COLUMN_MIME_TYPE:
                    row.add(Document.COLUMN_MIME_TYPE, determineMimeType(path));
                    break;
                case Document.COLUMN_DOCUMENT_ID:
                    row.add(Document.COLUMN_DOCUMENT_ID, path.toAbsolute());
                    break;
                case Document.COLUMN_DISPLAY_NAME:
                    row.add(Document.COLUMN_DISPLAY_NAME, path.getName());
                    break;
                case Document.COLUMN_SIZE:
                    final long fileSize = (long) provider.getAttribute(path, FileAttribute.FILESIZE);
                    row.add(Document.COLUMN_SIZE, fileSize);
                    break;
                case Document.COLUMN_LAST_MODIFIED:
                    final long lastModified = (long) provider.getAttribute(path, FileAttribute.LAST_MODIFIED);
                    if (lastModified != 0) {
                        row.add(Document.COLUMN_LAST_MODIFIED, lastModified);
                    }
                    break;
                case Document.COLUMN_FLAGS:
                    int flags = 0;
                    row.add(Document.COLUMN_FLAGS, flags);
                    break;
                default:
                    Log.w("Drive Mount", "Couldn't satisfy " + column + " column.");
            }
        }
    }

    private File getTemporaryName(Path path) throws IOException {
        final String filename = FilenameHash.getHash(path);
        final File cacheDir = getContext().getCacheDir();

        return File.createTempFile(filename, null, cacheDir);
    }

    private void writeToCacheFile(Path path, File destination) {
        final FileSystemProvider provider = getProvider(path);
        final FileHandle handle = provider.openDocument(path);

        FileOutputStream fos = null;
        InputStream is = null;
        try {
            fos = new FileOutputStream(destination);
            is = handle.readDocument();
            IOUtils.copy(is, fos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(fos);
        }
    }

    private FileSystemProvider getProvider(Path path) {
        return mStorageManager.getDevice(path).getProvider();
    }

    private String determineMimeType(Path path) {
        final FileSystemProvider provider = getProvider(path);

        if (provider.isDirectory(path)) {
            return Document.MIME_TYPE_DIR;
        }

        return MimeUtil.guessMimeType(path);
    }
}
