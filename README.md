# README

[![Build Status](https://travis-ci.org/alphaDev-net/drive-mount.svg?branch=dev)](https://travis-ci.org/alphaDev-net/drive-mount)

This app aims to provide access to USB Mass Storage devices on Android devices without requiring root permissions.

![SAF UsbDocProvider](https://cloud.githubusercontent.com/assets/1467318/5219580/0696d7ac-765a-11e4-9cfe-a53727d4323e.png)

This is achieved in pure Java, which trades performance for portability. *Better slow access, than having no access at all.*

## Requirements

- Android (SDK >=19)
  For the [Storage Access Framework API](https://developer.android.com/guide/topics/providers/document-provider.html).

- [USB Host](https://developer.android.com/guide/topics/connectivity/usb/host.html) Mode.
  For Access to the USB Device.

## Roadmap

For now it is planned to support common USB drive file formats like FAT32 and NTFS. However I'd like to support HFS and Linux file systems, sometime.

Please refer to the [Milestones](https://github.com/alphaDev-net/drive-mount/milestones) for a more detailed look ahead.

## Issues

If you have found a bug either report it via the Google Play Crash Handler or try to replicate the incident with logging enabled like so:

```
adb logcat
```

## Links

[External Resources](https://github.com/alphaDev-net/drive-mount/wiki/External-Resources)
