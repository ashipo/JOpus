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

extern "C" JNIEXPORT jint JNICALL
Java_com_fake_jopus_Opus_initDecoder(JNIEnv *env, jobject, jint sampleRate, jint numChannels) {
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

extern "C" JNIEXPORT jint JNICALL
Java_com_fake_jopus_Opus_decode(JNIEnv *env, jobject, jbyteArray encodedData, jint encodedBytes, jbyteArray decodedData, jint decodedFrames, jint fec) {
    auto *nativeEncodedData = reinterpret_cast<jbyte *>(env->GetPrimitiveArrayCritical(encodedData, 0));
    auto *nativeDecodedData = reinterpret_cast<jbyte *>(env->GetPrimitiveArrayCritical(decodedData, 0));
    const int result = opus_decode(decoder_.get(),
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

extern "C" JNIEXPORT jint JNICALL
Java_com_fake_jopus_Opus_plc(JNIEnv *env, jobject, jbyteArray decodedData, jint decodedFrames, jint fec) {
    auto *nativeDecodedData = reinterpret_cast<jbyte *>(env->GetPrimitiveArrayCritical(decodedData, 0));
    const int result = opus_decode(decoder_.get(),
                                   NULL,
                                   0,
                                   reinterpret_cast<opus_int16 *>(nativeDecodedData),
                                   decodedFrames,
                                   fec);
    env->ReleasePrimitiveArrayCritical(decodedData, nativeDecodedData, 0);

    if (result < 0) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Decode PLC error: %s", opus_strerror(result));
    }
    return result;
}

extern "C" JNIEXPORT void JNICALL
Java_com_fake_jopus_Opus_releaseDecoder(JNIEnv *env, jobject) {
    decoder_.reset();
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_fake_jopus_Opus_strerror(JNIEnv* env, jobject, jint error) {
    return env->NewStringUTF(opus_strerror(error));
}
