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

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public enum FileSystemDescriptor {
    /**
     * Unused.
     * (0x00)
     */
    UNUSED,

    /**
     * FAT12 for Floppy Disks.
     * (0x01)
     */
    FAT12,

    /**
     * FAT16 ≤ 32MB.
     * (0x04)
     */
    FAT16,

    /**
     * Extended Partition.
     * (0x05)
     */
    EXTENDED,

    /**
     * FAT16 > 32MB.
     * (0x06)
     */
    FAT16_LARGE,

    /**
     * NTFS (Windows NT/2000/XP/Vista/7/8).
     * HPFS (OS/2).
     * exFAT.
     * (0x07)
     */
    NTFS,

    /**
     * FAT32.
     * (0x0b)
     */
    FAT32,

    /**
     * FAT32 with LBA.
     * (0x0c)
     */
    FAT32_LBA,

    /**
     * FAT16 > 32MB with LBA.
     * (0x0e)
     */
    FAT16_LBA,

    /**
     * Extended Partition with LBA.
     * (0x0f)
     */
    EXTENDED_LBA,

    /**
     * OEM Partition.
     * (0x12)
     */
    OEM,

    /**
     * Windows RE (hidden).
     * (0x27)
     */
    WINDOWS_RE,

    /**
     * Dynamic Drive.
     * (0x42)
     */
    DYNAMIC,

    /**
     * Linux Swap.
     * (0x82)
     */
    LINUX_SWAP,

    /**
     * Linux Native.
     * (0x83)
     */
    LINUX_NATIVE,

    /**
     * Linux LVM.
     * (0x8e)
     */
    LINUX_LVM,

    /**
     * FreeBSD.
     * (0xa5)
     */
    FREEBSD,

    /**
     * OpenBSD.
     * (0xa6)
     */
    OPENBSD,

    /**
     * NetBSD.
     * (0xa9)
     */
    NETBSD,

    /**
     * Legacy MBR with EFI-Header.
     * (0xee)
     */
    LEGACY_MBR_EFI,

    /**
     * EFI Filesystem.
     * (0xef)
     */
    EFI;

    public static FileSystemDescriptor parse(int fsd) {
        switch (fsd) {
            case 0x00:
                return UNUSED;
            case 0x01:
                return FAT12;
            case 0x04:
                return FAT16;
            case 0x05:
                return EXTENDED;
            case 0x06:
                return FAT16_LARGE;
            case 0x07:
                return NTFS;
            case 0x0b:
                return FAT32;
            case 0x0c:
                return FAT32_LBA;
            case 0x0e:
                return FAT16_LBA;
            case 0x0f:
                return EXTENDED_LBA;
            case 0x12:
                return OEM;
            case 0x27:
                return WINDOWS_RE;
            case 0x42:
                return DYNAMIC;
            case 0x82:
                return LINUX_SWAP;
            case 0x83:
                return LINUX_NATIVE;
            case 0x8e:
                return LINUX_LVM;
            case 0xa5:
                return FREEBSD;
            case 0xa6:
                return OPENBSD;
            case 0xa9:
                return NETBSD;
            case 0xee:
                return LEGACY_MBR_EFI;
            case 0xef:
                return EFI;
        }
        throw new UnsupportedOperationException();
    }
}
