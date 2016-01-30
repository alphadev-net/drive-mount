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
package net.alphadev.usbstorage.scsi;

import net.alphadev.usbstorage.util.BitStitching;

import java.nio.ByteOrder;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class CommandStatusWrapper {
    public static final String USB_STATUS_SIGNATURE = "USBS";

    private byte[] mSignature;
    private int mTag;
    private int mDataResidue;
    private byte mStatus;

    public CommandStatusWrapper(byte[] data) {
        if (data.length != 13) {
            throw new IllegalArgumentException("CSW always has a length of 13 bytes!");
        }

        mSignature = new byte[4];
        mSignature[0x0] = data[0x0];
        mSignature[0x1] = data[0x1];
        mSignature[0x2] = data[0x2];
        mSignature[0x3] = data[0x3];

        if (!USB_STATUS_SIGNATURE.equals(getSignature())) {
            System.out.println(BitStitching.convertBytesToHex(data));
            throw new IllegalArgumentException("Invalid CSW header!");
        }

        mTag = BitStitching.convertToInt(data, 0x4, ByteOrder.LITTLE_ENDIAN);
        mDataResidue = BitStitching.convertToInt(data, 0x8, ByteOrder.LITTLE_ENDIAN);
        mStatus = data[0xc];
    }

    public String getSignature() {
        return new String(mSignature);
    }

    public int getTag() {
        return mTag;
    }

    public int getDataResidue() {
        return mDataResidue;
    }

    public Status getStatus() {
        switch (mStatus) {
            case 0:
                return Status.COMMAND_PASSED;
            case 2:
                return Status.CHECK_CONDITION;
            case 4:
                return Status.CONDITION_MET;
            case 8:
                return Status.BUSY;
            case 16:
                return Status.INTERMEDIATE;
            case 20:
                return Status.INTERMEDIATE_CONDITION_MET;
            case 24:
                return Status.RESERVATION_CONFLICT;
            case 34:
                return Status.COMMAND_TERMINATED;
            case 40:
                return Status.QUEUE_FULL;
            default:
                return Status.RESERVED;
        }
    }

    public enum Status {
        /**
         * This status indicates that the target has successfully completed the command.
         */
        COMMAND_PASSED,

        /**
         * This status indicates that a contingent allegiance condition has occurred.
         */
        CHECK_CONDITION,

        /**
         * This status or INTERMEDIATE-CONDITION MET is returned whenever the requested operation is
         * satisfied (see the SEARCH DATA and PRE-FETCH commands).
         */
        CONDITION_MET,

        /**
         * This status indicates that the target is busy.  This status shall be returned whenever a
         * target is unable to accept a command from an otherwise acceptable initiator (i.e., no
         * reservation conflicts).  The recommended initiator recovery action is to issue the
         * command again at a later time.
         */
        BUSY,

        /**
         * This status or INTERMEDIATE-CONDITION MET shall be returned for every successfully
         * completed command in a series of linked commands (except the last command), unless the
         * command is terminated with CHECK CONDITION, RESERVATION CONFLICT, or COMMAND TERMINATED
         * status.  If INTERMEDIATE or INTERMEDIATE-CONDITION MET status is not returned, the series
         * of linked commands is terminated and the I/O process is ended.
         */
        INTERMEDIATE,

        /**
         * This status is the combination of the CONDITION MET and INTERMEDIATE statuses.
         */
        INTERMEDIATE_CONDITION_MET,

        /**
         * This status shall be returned whenever an initiator attempts to access a logical unit or
         * an extent within a logical unit that is reserved with a conflicting reservation type for
         * another SCSI device (see the RESERVE and RESERVE UNIT commands).  The recommended
         * initiator recovery action is to issue the command again at a later time.
         */
        RESERVATION_CONFLICT,

        /**
         * This status shall be returned whenever the target terminates the current I/O process
         * after receiving a TERMINATE I/O PROCESS message (see 5.6.22). This status also indicates
         * that a contingent allegiance condition has occurred (see 6.6).
         */
        COMMAND_TERMINATED,

        /**
         * This status shall be implemented if tagged queuing is implemented. This status is
         * returned when a SIMPLE QUEUE TAG, ORDERED QUEUE TAG, or HEAD OF QUEUE TAG message is
         * received and the command queue is full. The I/O process is not placed in the command
         * queue.
         */
        QUEUE_FULL,

        /**
         * Reserved for future use.
         */
        RESERVED
    }
}
