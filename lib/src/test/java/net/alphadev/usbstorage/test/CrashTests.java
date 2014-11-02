package net.alphadev.usbstorage.test;

import net.alphadev.usbstorage.scsi.answer.StandardInquiryAnswer;

import org.junit.Test;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class CrashTests {
    @Test
    public void testIssue7() {
        byte[] data = new byte[]{ // WD My Passport SIA
                0, 0, 6, 0x12, 0x5b, 0, 0, 0, 0x57, 0x44, 0x20, 0x20, 0x20, 0x20,
                0x20, 0x20, 0x4d, 0x79, 0x20, 0x50, 0x61, 0x73, 0x73, 0x70, 0x6f,
                0x72, 0x74, 0x20, 0x30, 0x38, 0x33, 0x30, 0x31, 0x30, 0x35, 0x36
        };

        new StandardInquiryAnswer(data);
    }
}