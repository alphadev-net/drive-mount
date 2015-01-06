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
package net.alphadev.usbstorage.partition;

import net.alphadev.usbstorage.api.device.BlockDevice;
import net.alphadev.usbstorage.util.BitStitching;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class MasterBootRecord {
    private static final int SIGNATURE_OFFSET = 0x01FE;
    private static final int PARTITION_DATA_OFFSET = 0x1BE;
    private static final int PARTITION_DATA_LENGTH = 16;

    private final HashSet<Partition> mPartitions;

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
            System.out.println(BitStitching.convertByteBufferToHex(buffer));
            throw new IllegalArgumentException("no mbr signature found!");
        }

        for (byte entry = 0; entry < 4; entry++) {
            byte[] data = new byte[PARTITION_DATA_LENGTH];
            int offset = PARTITION_DATA_OFFSET + entry * PARTITION_DATA_LENGTH;
            buffer.position(offset);
            buffer.get(data);

            PartitionParameters param = new PartitionParameters(data, entry);
            if (param.getDescriptor() != FileSystemDescriptor.UNUSED) {
                Partition partition = new Partition(device, param);
                mPartitions.add(partition);
            }
        }
    }

    public Iterable<Partition> getPartitions() {
        return mPartitions;
    }
}
