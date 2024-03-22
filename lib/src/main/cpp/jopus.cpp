#include <jni.h>
#include <string>
#include <android/log.h>

#include "opus.h"

OpusDecoder *opusDecoder = nullptr;
const char *TAG = "JOpus";

extern "C" JNIEXPORT jint JNICALL
Java_com_fake_jopus_Opus_initDecoder(JNIEnv *env, jobject, jint sampleRate, jint numChannels) {
    int error;
    opusDecoder = opus_decoder_create(sampleRate, numChannels, &error);
    if (error != OPUS_OK) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Init decoder error: %s", opus_strerror(error));
    }
    return (jint)error;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_fake_jopus_Opus_decode(JNIEnv *env, jobject, jbyteArray encodedData, jint encodedBytes, jbyteArray decodedData, jint decodedFrames, jint fec) {
    auto *nativeEncodedData = reinterpret_cast<jbyte *>(env->GetPrimitiveArrayCritical(encodedData, 0));
    auto *nativeDecodedData = reinterpret_cast<jbyte *>(env->GetPrimitiveArrayCritical(decodedData, 0));
    const int result = opus_decode(opusDecoder,
                                   reinterpret_cast<const unsigned char *>(nativeEncodedData),
                                   encodedBytes,
                                   reinterpret_cast<opus_int16 *>(nativeDecodedData),
                                   decodedFrames,
                                   fec);

    env->ReleasePrimitiveArrayCritical(decodedData, nativeDecodedData, 0);
    env->ReleasePrimitiveArrayCritical(encodedData, nativeEncodedData, JNI_ABORT);

    if (result < 0) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Decode error: %s", opus_strerror(result));
    }
    return result;
}

extern "C" JNIEXPORT void JNICALL
Java_com_fake_jopus_Opus_releaseDecoder(JNIEnv *env, jobject) {
    if (opusDecoder) opus_decoder_destroy(opusDecoder);
    opusDecoder = nullptr;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_fake_jopus_Opus_strerror(JNIEnv* env, jobject, jint error) {
    return env->NewStringUTF(opus_strerror(error));
}
