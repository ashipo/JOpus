#include <jni.h>
#include <string>
#include <memory>
#include <android/log.h>

#include "opus.h"

const char *TAG = "JOpus";

struct DecoderDeleter {
    void operator()(OpusDecoder* decoder) const {
        opus_decoder_destroy(decoder);
    }
};
using Decoder = std::unique_ptr<OpusDecoder, DecoderDeleter&>;
DecoderDeleter decoderDeleter_;
Decoder decoder_{ nullptr, decoderDeleter_ };

jint JNICALL initDecoder(JNIEnv *env, jobject, jint sampleRate, jint numChannels) {
    int error{};
    decoder_ = Decoder(
        opus_decoder_create(sampleRate, numChannels, &error),
        decoderDeleter_
    );
    if (error != OPUS_OK) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Init decoder error: %s", opus_strerror(error));
    }
    return (jint)error;
}

void JNICALL releaseDecoder(JNIEnv *env, jobject) {
    decoder_.reset();
}

jstring JNICALL getErrorString(JNIEnv* env, jobject, jint error) {
    return env->NewStringUTF(opus_strerror(error));
}

jint JNICALL decode(JNIEnv *env, jobject, jbyteArray encodedData, jint encodedBytes, jbyteArray decodedData, jint decodedFrames, jint fec) {
    auto *nativeEncodedData = reinterpret_cast<jbyte *>(env->GetPrimitiveArrayCritical(encodedData, 0));
    auto *nativeDecodedData = reinterpret_cast<jbyte *>(env->GetPrimitiveArrayCritical(decodedData, 0));
    const int result = opus_decode(
            decoder_.get(),
            reinterpret_cast<const unsigned char *>(nativeEncodedData),
            encodedBytes,
            reinterpret_cast<opus_int16 *>(nativeDecodedData),
            decodedFrames,
            fec
    );
    env->ReleasePrimitiveArrayCritical(decodedData, nativeDecodedData, 0);
    env->ReleasePrimitiveArrayCritical(encodedData, nativeEncodedData, JNI_ABORT);

    if (result < 0) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Decode error: %s", opus_strerror(result));
    }
    return result;
}

jint JNICALL plc(JNIEnv *env, jobject, jbyteArray decodedData, jint decodedFrames, jint fec) {
    auto *nativeDecodedData = reinterpret_cast<jbyte *>(env->GetPrimitiveArrayCritical(decodedData, 0));
    const int result = opus_decode(
            decoder_.get(),
            nullptr,
            0,
            reinterpret_cast<opus_int16 *>(nativeDecodedData),
            decodedFrames,
            fec
    );
    env->ReleasePrimitiveArrayCritical(decodedData, nativeDecodedData, 0);

    if (result < 0) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Decode PLC error: %s", opus_strerror(result));
    }
    return result;
}

// https://developer.android.com/training/articles/perf-jni#native-libraries
JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    jclass c = env->FindClass("com/fake/jopus/Opus");
    if (c == nullptr) return JNI_ERR;

    static const JNINativeMethod methods[] = {
        {"initDecoder", "(II)I", reinterpret_cast<void*>(initDecoder)},
        {"releaseDecoder", "()V", reinterpret_cast<void*>(releaseDecoder)},
        {"strerror", "(I)Ljava/lang/String;", reinterpret_cast<void*>(getErrorString)},
        {"decode", "([BI[BII)I", reinterpret_cast<void*>(decode)},
        {"plc", "([BII)I", reinterpret_cast<void*>(plc)},
    };
    int rc = env->RegisterNatives(c, methods, sizeof(methods)/sizeof(JNINativeMethod));
    if (rc != JNI_OK) return rc;

    return JNI_VERSION_1_6;
}
