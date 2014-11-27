# README

This app aims to provide access to USB Mass Storage devices on Android devices without requiring root permissions.

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
