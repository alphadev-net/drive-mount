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

import net.alphadev.usbstorage.api.StorageDevice;

import java.io.File;
import java.io.FileNotFoundException;

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

    @Override
    public boolean onCreate() {
        mStorageManager = new StorageManager();
        DeviceManager deviceManager = new DeviceManager(getContext(), mStorageManager);
        deviceManager.setOnStorageChangedListener(new DeviceManager.OnStorageChangedListener() {
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
    public Cursor queryRoots(String[] projection) throws FileNotFoundException {
        final MatrixCursor roots =
                new MatrixCursor(resolveRootProjection(projection));

        for (StorageDevice device : mStorageManager.getMounts()) {
            createDevice(roots.newRow(), device, projection);
        }

        return roots;
    }

    private void createDevice(MatrixCursor.RowBuilder row, StorageDevice device, String[] projection) {
        for (String column : projection) {
            switch (column) {
                case Root.COLUMN_ROOT_ID:
                    Log.i("Drive Mount", "id: " + device.getId());
                    row.add(Root.COLUMN_ROOT_ID, device.getId());
                    break;
                case Root.COLUMN_TITLE:
                    row.add(Root.COLUMN_TITLE, device.getDeviceName());
                    Log.i("Drive Mount", "name: " + device.getDeviceName());
                    break;
                case Root.COLUMN_ICON:
                    row.add(Root.COLUMN_ICON, R.drawable.drive_icon_gen);
                    break;
                case Root.COLUMN_SUMMARY:
                    row.add(Root.COLUMN_SUMMARY, device.getStorageDetails());
                    break;
                default:
                    Log.w("Drive Mount", "Couldn't satisfy " + column + " column.");
            }
        }
    }

    @Override
    public Cursor queryDocument(String documentId, String[] projection) throws FileNotFoundException {
        final MatrixCursor result = new
                MatrixCursor(resolveDocumentProjection(projection));
        return result;
    }

    @Override
    public Cursor queryChildDocuments(String parentDocumentId, String[] projection, String p3) throws FileNotFoundException {
        final MatrixCursor result = new
                MatrixCursor(resolveDocumentProjection(projection));
        return result;
    }

    @Override
    public ParcelFileDescriptor openDocument(final String documentId,
                                             final String mode,
                                             CancellationSignal signal) throws FileNotFoundException {
        return null;
    }

    @SuppressWarnings("unused")
    private File getFileForDocId(String documentId) {
        return null;
    }
}
