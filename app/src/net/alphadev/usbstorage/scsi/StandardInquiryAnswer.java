package net.alphadev.usbstorage.scsi;

/**
 * Created by jan on 28.08.14.
 */
public class StandardInquiryAnswer {
    private byte mPeripheralQualifier;
    private byte mPeripheralDeviceType;

    public StandardInquiryAnswer(byte[] bytes) {
        if(bytes.length != Inquiry.LENGTH) {
            throw new IllegalArgumentException("Inquiry answer has invalid length!");
        }

        //mPeripheralDeviceType =
    }
}
