package net.alphadev.usbstorage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.hardware.usb.UsbManager;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.DocumentsContract.Document;
import android.provider.DocumentsContract.Root;
import android.provider.DocumentsProvider;

import java.io.File;
import java.io.FileNotFoundException;

public class DocumentProviderImpl extends DocumentsProvider {

	private static final String AUTHORITY = "net.alphadev.usbstorage.documents";

    private static final String[] DEFAULT_ROOT_PROJECTION = new String[] {
		Root.COLUMN_ROOT_ID, Root.COLUMN_FLAGS, Root.COLUMN_ICON, Root.COLUMN_TITLE,
		Root.COLUMN_DOCUMENT_ID, Root.COLUMN_AVAILABLE_BYTES, Root.COLUMN_SUMMARY
    };
    private static final String[] DEFAULT_DOCUMENT_PROJECTION = new String[] {
		Document.COLUMN_DOCUMENT_ID, Document.COLUMN_MIME_TYPE, Document.COLUMN_DISPLAY_NAME,
		Document.COLUMN_LAST_MODIFIED, Document.COLUMN_FLAGS, Document.COLUMN_SIZE,
    };

    private static String[] resolveRootProjection(String[] projection) {
        return projection != null ? projection : DEFAULT_ROOT_PROJECTION;
    }

    private static String[] resolveDocumentProjection(String[] projection) {
        return projection != null ? projection : DEFAULT_DOCUMENT_PROJECTION;
    }

    private StorageManager mStorageManager;

	@Override
	public boolean onCreate() {
        mStorageManager = new StorageManager(getContext());
		mStorageManager.enumerateDevices();
		mStorageManager.setOnStorageChangedListener(new StorageManager.OnStorageChangedListener() {
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

		mStorageManager.enumerateDevices();
        for (StorageDevice device : mStorageManager.getStorageDevices()) {
            createDevice(roots.newRow(), device);
        }

		return roots;
	}

	private void createDevice(MatrixCursor.RowBuilder row, StorageDevice device) {
		row.add(Root.COLUMN_ROOT_ID, device.getDeviceName());
		row.add(Root.COLUMN_ICON, R.drawable.drive_icon);
		row.add(Root.COLUMN_SUMMARY, device.getStorageDetails());
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

	private File getFileForDocId(String documentId) {
		return null;
	}
}
