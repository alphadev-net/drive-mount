package net.alphadev.usbstorage.partition;

import net.alphadev.usbstorage.api.BlockDevice;
import net.alphadev.usbstorage.api.Identifiable;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class MasterBootRecord implements Identifiable {
    private Iterable<? extends BlockDevice> partitions;

    public MasterBootRecord(BlockDevice device) {

    }

    public Iterable<? extends BlockDevice> getPartitions() {
        return partitions;
    }

    @Override
    public int getId() {
        return 0;
    }
}
