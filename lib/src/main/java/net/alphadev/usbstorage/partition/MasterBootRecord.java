package net.alphadev.usbstorage.partition;

import net.alphadev.usbstorage.api.BlockDevice;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class MasterBootRecord {
    public static final int SIGNATURE_OFFSET = 0x01FE;
    public static final int PARTITION_DATA_OFFSET = 0x1BE;
    public static final int PARTITION_DATA_LENGTH = 16;

    private HashSet<Partition> mPartitions;

    public MasterBootRecord(BlockDevice device) {
        mPartitions = new HashSet<>();
        ByteBuffer buffer = ByteBuffer.allocate(512);

        try {
            device.read(0, buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if ((buffer.get(SIGNATURE_OFFSET) & 0xff) != 0x55 ||
                (buffer.get(SIGNATURE_OFFSET + 1) & 0xff) != 0xaa) {
            throw new IllegalArgumentException("no mbr signature found!");
        }

        for (int entry = 0; entry < 4; entry++) {
            byte[] data = new byte[PARTITION_DATA_LENGTH];
            int offset = PARTITION_DATA_OFFSET + entry * PARTITION_DATA_LENGTH;
            buffer.position(offset);
            buffer.get(data);

            PartitionParameters param = new PartitionParameters(data);
            Partition partition = new Partition(device, entry, param);
            mPartitions.add(partition);
        }
    }

    public Iterable<Partition> getPartitions() {
        return mPartitions;
    }
}
