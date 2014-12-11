package com.commonsware.android.advservice;

import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ParcelFileDescriptorUtil {
    private static final int MAX_BUFFER = 4096;

    public static ParcelFileDescriptor pipeFrom(InputStream inputStream)
            throws IOException {
        final ParcelFileDescriptor[] pipe = ParcelFileDescriptor.createPipe();
        final OutputStream output = new ParcelFileDescriptor.AutoCloseOutputStream(pipe[1]);

        new TransferThread(inputStream, output).start();

        return pipe[0];
    }

    @SuppressWarnings("unused")
    public static ParcelFileDescriptor pipeTo(OutputStream outputStream)
            throws IOException {
        final ParcelFileDescriptor[] pipe = ParcelFileDescriptor.createPipe();
        final InputStream input = new ParcelFileDescriptor.AutoCloseInputStream(pipe[0]);

        new TransferThread(input, outputStream).start();

        return pipe[1];
    }

    static class TransferThread extends Thread {
        final InputStream mIn;
        final OutputStream mOut;

        TransferThread(InputStream in, OutputStream out) {
            super("ParcelFileDescriptor Transfer Thread");
            mIn = in;
            mOut = out;
            setDaemon(true);
        }

        @Override
        public void run() {
            byte[] buf = new byte[MAX_BUFFER];
            int len;

            try {
                while ((len = mIn.read(buf)) > 0) {
                    mOut.write(buf, 0, len);
                }
                mOut.flush(); // just to be safe
            } catch (IOException e) {
                Log.e("TransferThread", "writing failed");
                e.printStackTrace();
            } finally {
                try {
                    mIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    mOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}