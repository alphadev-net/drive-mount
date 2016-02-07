#include "blockdevice.h"

BlockDevice::BlockDevice(JNIEnv *jenv, jobject blockDevice) :
        jniEnv(jenv),
        blockDevice(blockDevice) {
}

void BlockDevice::read(char *buffer, long bufSize, long offset) {
    jobject jbuffer = jniEnv->NewDirectByteBuffer(buffer, bufSize);
    jniEnv->CallVoidMethod(blockDevice, blockDevice_readMethod, bufSize, jbuffer);
}

void BlockDevice::flush() {

}

void BlockDevice::close() {

}

jint JNI_OnLoad(JavaVM *vm, void* /*reserved*/) {
    JNIEnv *env;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    } else {
        jclass localBufferCls = env->FindClass(BLOCKDEVICE);
        blockDeviceClass = (jclass) env->NewGlobalRef(localBufferCls);
        blockDevice_readMethod = env->GetMethodID(blockDeviceClass, "read", READ_SIGNATURE);
    }
    return 0;
}

void JNI_OnUnload(JavaVM *vm, void* /*reserved*/) {
    JNIEnv *env;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return;
    } else {
        if (blockDeviceClass != nullptr) {
            env->DeleteGlobalRef(blockDeviceClass);
        }
    }
}
