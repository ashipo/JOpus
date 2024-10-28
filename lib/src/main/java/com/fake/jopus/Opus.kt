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

    /**
     * Initiates the decoder
     *
     * @param sampleRate sample rate to decode at (Hz). Must be one of 8000, 12000, 16000, 24000
     * or 48000
     * @param channels number of channels to decode (1 or 2)
     * @return [OPUS_OK] on success or an error code
     */
    external fun initDecoder(sampleRate: Int, channels: Int): Int

    /**
     * Destroys the decoder
     */
    @FastNative
    external fun releaseDecoder()

    /**
     * Converts an Opus error code into a human readable string
     *
     * @param error error code
     * @return error string
     */
    @FastNative
    external fun getErrorString(error: Int): String

    /**
     * Decodes an Opus packet
     *
     * @param input input payload
     * @param inputBytes number of bytes in payload
     * @param output output signal (interleaved if 2 channels). Length is
     * `outputFrames * channels * sizeof(int16)`
     * @param outputFrames number of samples per channel of available space in `output`.
     * In the case of FEC (`fec`=1), `outputFrames` needs to be exactly the duration of audio that
     * is missing, otherwise the decoder will not be in the optimal state to decode the next
     * incoming packet. For the FEC case, must be a multiple of 2.5 ms.
     * @param fec Flag (0 or 1) to request that any in-band forward error correction data be decoded.
     * If no such data is available, the frame is decoded as if it were lost.
     * @return number of decoded frames or an error code
     */
    @FastNative
    external fun decode(
        input: ByteArray,
        inputBytes: Int,
        output: ByteArray,
        outputFrames: Int,
        fec: Int
    ): Int

    /**
     * Opus packet loss concealment (PLC).
     *
     * @param output output signal (interleaved if 2 channels). Length is
     * `outputFrames * channels * sizeof(int16)`
     * @param outputFrames number of samples per channel of available space in `output`.
     * Needs to be exactly the duration of audio that is missing, otherwise the decoder will not be
     * in the optimal state to decode the next incoming packet. Must be a multiple of 2.5 ms.
     * @return number of decoded frames or an error code
     */
    @FastNative
    external fun plc(
        output: ByteArray,
        outputFrames: Int,
    ): Int
}
