#pragma once

#include <jni.h>

#define BLOCKDEVICE "net/alphadev/usbstorage/api/device/BlockDevice"
#define READ_SIGNATURE "(JLjava.nio.ByteBuffer)V"

jclass blockDeviceClass;
jmethodID blockDevice_readMethod;

jint JNI_OnLoad(JavaVM* vm, void* reserved);

void JNI_OnUnload(JavaVM *vm, void *reserved);

class BlockDevice {
public:
    BlockDevice(JNIEnv *jenv, jobject blockDevice);

    virtual void read(char *buffer, long bufSize, long offset);

    virtual void flush();

    virtual void close();

private:
    JNIEnv* jniEnv;
    jobject blockDevice;
};