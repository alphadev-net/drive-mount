/**
 * Copyright © 2016 Jan Seeger
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
#include <jni.h>

JNIEXPORT void JNICALL Java_net_alphadev_fat32wrapper_FatStorage_jniOpen(JNIEnv* /*env*/, jobject /*instance*/, jobject /*blockDevice*/) {
}

JNIEXPORT jstring JNICALL Java_net_alphadev_fat32wrapper_FatStorage_jniGetName(JNIEnv* env, jobject /*instance*/) {
    return env->NewStringUTF("");
}

JNIEXPORT jlong JNICALL Java_net_alphadev_fat32wrapper_FatStorage_jniGetTotalSpace(JNIEnv* /*env*/, jobject /*instance*/) {
    return 0l;
}

JNIEXPORT jlong JNICALL Java_net_alphadev_fat32wrapper_FatStorage_jniGetFreeSpace(JNIEnv* /*env*/, jobject /*instance*/) {
    return 0l;
}

JNIEXPORT jlong JNICALL Java_net_alphadev_fat32wrapper_FatStorage_jniGetUsableSpace(JNIEnv* /*env*/, jobject /*instance*/) {
    return 0l;
}

JNIEXPORT void JNICALL Java_net_alphadev_fat32wrapper_FatStorage_jniClose(JNIEnv* /*env*/, jobject /*instance*/) {
}

JNIEXPORT jstring JNICALL Java_net_alphadev_fat32wrapper_FatStorage_jniGetType(JNIEnv *env, jobject /*instance*/) {
    return env->NewStringUTF("");
}