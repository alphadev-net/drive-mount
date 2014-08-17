package net.alphadev.usbstorage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.DocumentsContract.Document;
import android.provider.DocumentsContract.Root;
import android.provider.DocumentsProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

public class DocumentProviderImpl extends DocumentsProvider {

	private static final String AUTHORITY = "net.alphadev.usbstorage.documents";
    BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            context.getContentResolver()
                    .notifyChange(DocumentsContract
                            .buildRootsUri(AUTHORITY), null);
        }
    };
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

	@Override
	public boolean onCreate() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		getContext().registerReceiver(mUsbReceiver, filter);
		return true;
	}

	@Override
	public Cursor queryRoots(String[] projection) throws FileNotFoundException {
		final MatrixCursor roots =
            new MatrixCursor(resolveRootProjection(projection));

		for (UsbDevice drive: enumerateDrives().values()) {
			final MatrixCursor.RowBuilder row = roots.newRow();
			row.add(Root.COLUMN_ROOT_ID, drive.getDeviceName());
            row.add(Root.COLUMN_ICON, R.drawable.drive_icon);
            //row.add(Root.COLUMN_SUMMARY, drive.getStorageDetails());
		}

		return roots;
	}

	private HashMap<String, UsbDevice> enumerateDrives() {
		UsbManager manager = (UsbManager) getContext().getSystemService(Context.USB_SERVICE);
		return manager.getDeviceList();
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