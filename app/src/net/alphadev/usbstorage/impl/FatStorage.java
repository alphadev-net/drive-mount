package net.alphadev.usbstorage.impl;

import de.waldheinz.fs.fat.FatFileSystem;
import net.alphadev.usbstorage.StorageDevice;
import net.alphadev.usbstorage.UsbBlockDevice;
import java.io.IOException;

public class FatStorage implements StorageDevice {

	private final FatFileSystem fs;

	public FatStorage(UsbBlockDevice blockDevice) throws IOException {
		fs = FatFileSystem.read(blockDevice, true);
	}

	@Override
	public String getDeviceName() {
		return fs.getVolumeLabel();
	}

	@Override
	public StorageDetails getStorageDetails() {
		return new StorageDetails() {
			@Override
			public long getTotalSpace() {
				return fs.getTotalSpace();
			}

			@Override
			public long getFreeSpace() {
				return fs.getFreeSpace();
			}
		};
	}

	@Override
	public FsType getFsType() {
		return FsType.FAT;
	}
}
