package net.alphadev.usbstorage.scsi;

/**
 * Created by jan on 26.08.14.
 */
public class CommandBlockWrapper {
    private static int tagCounter = 0;

    private static byte getLength(byte data) {
        if(data < 0x20) {
            return 6;
        } else if(data >= 0x20 && data < 0x60) {
            return 10;
        } else if(data >= 0x80 && data < 0xa0) {
            return 16;
        } else if(data >= 0xa0 && data < 0xc0) {
            return 12;
        }
        return 0;
    }

    private byte[] signature;
    private int tag;
    private int dataTransferLength;
    private byte flags;
    private byte LUN;
    private byte cmdBlockLength;
    private byte[] cmdBlock;

    public CommandBlockWrapper() {
        cmdBlock = new byte[16];
        tag = tagCounter++;
    }

    public void setSignature(byte a, byte b, byte c, byte d) {
        signature = new byte[]{a, b, c, d};
    }

    public byte[] asBytes() {
        return new byte[]{
                signature[0],
                signature[1],
                signature[2],
                signature[3],

                (byte) tag,                 // first 8 bit of tag
                (byte) (tag >>> 8),         // second 8 bit of tag
                (byte) (tag >>> 16),        // third 8 bit of tag
                (byte) (tag >>> 24),        // fourth 8 bit of tag

                (byte) dataTransferLength,
                (byte) (dataTransferLength >>> 8),
                (byte) (dataTransferLength >>> 16),
                (byte) (dataTransferLength >>> 24),

                flags,
                LUN,
                cmdBlockLength,

                cmdBlock[0], cmdBlock[1], cmdBlock[2], cmdBlock[3],
                cmdBlock[4], cmdBlock[5], cmdBlock[6], cmdBlock[7],
                cmdBlock[8], cmdBlock[9], cmdBlock[10], cmdBlock[11],
                cmdBlock[12], cmdBlock[13], cmdBlock[14], cmdBlock[15]
        };
    }
}
