package com.fake.jopus

import dalvik.annotation.optimization.FastNative

const val OPUS_OK = 0
const val OPUS_BAD_ARG = -1
const val OPUS_BUFFER_TOO_SMALL = -2
const val OPUS_INTERNAL_ERROR = -3
const val OPUS_INVALID_PACKET = -4
const val OPUS_UNIMPLEMENTED = -5
const val OPUS_INVALID_STATE = -6
const val OPUS_ALLOC_FAIL = -7

class Opus {

    companion object {
        init {
            System.loadLibrary("jopus")
        }
    }

    external fun initDecoder(sampleRate: Int, numChannels: Int): Int

    @FastNative
    external fun releaseDecoder()

    @FastNative
    external fun strerror(error: Int): String

    @FastNative
    external fun decode(
        encodedData: ByteArray,
        encodedBytes: Int,
        decodedData: ByteArray,
        decodedFrames: Int,
        fec: Int
    ): Int

    @FastNative
    external fun plc(
        decodedData: ByteArray,
        decodedFrames: Int,
        fec: Int
    ): Int
}
