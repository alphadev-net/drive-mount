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
package net.alphadev.usbstorage.scsi.command;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
@SuppressWarnings("unused")
public class ModeSense extends ScsiCommand {
    public static final byte MODE_SENSE = 0x1a;
    /**
     * DBD (disable block descriptors) bit
     * Ifset to zero specifies that the device server may return zero or more block descriptors in the returned MODE SENSE data.
     * A DBD bit set to one specifies that the device server shall not return any block descriptors in the returned MODE SENSE data,
     */
    private boolean mDisableBlockDescriptor;
    private byte mPageCode;
    private byte mSubPageCode;
    private PageControlValues mPageControl;

    @Override
    public byte[] asBytes() {
        byte[] retval = new byte[6];
        retval[0] = MODE_SENSE; // opcode
        retval[1] = (byte) (mDisableBlockDescriptor ? 1 : 0); // DBD bit
        retval[2] = getPageField();
        retval[3] = mSubPageCode;
        retval[4] = (byte) 192;
        // 5 == control bit
        return retval;
    }

    private byte getPageField() {
        byte retval = 0;

        switch (mPageControl) {
            case Changeable:
                retval = 64;
                break;
            case Default:
                retval = (byte) 128;
                break;
            case Saved:
                retval = (byte) 192;
                break;
        }

        return (byte) (retval + mPageCode);
    }

    @Override
    public int getExpectedAnswerLength() {
        return 192;
    }

    public void setDisableBlockDescriptor(boolean value) {
        this.mDisableBlockDescriptor = value;
    }

    public void setPageCode(byte pageCode) {
        this.mPageCode = pageCode;
    }

    public void setSubPageCode(byte subPageCode) {
        this.mSubPageCode = subPageCode;
    }

    public void setPageControl(PageControlValues pageControl) {
        this.mPageControl = pageControl;
    }

    public static enum PageControlValues {
        Current,
        Changeable,
        Default,
        Saved
    }
}
