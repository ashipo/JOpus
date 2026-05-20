package ashipo.jopus

import com.google.testing.junit.testparameterinjector.KotlinTestParameters.testValues
import com.google.testing.junit.testparameterinjector.KotlinTestParameters.testValuesIn
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class OpusTest {

    private val validSampleRates = listOf(8_000, 12_000, 16_000, 24_000, 48_000)
    private val validChannels = listOf(1, 2)

    @Test
    fun initDecoder_ValidArgs_ReturnsOK(
        @TestParameter sampleRate: Int = testValuesIn(validSampleRates),
        @TestParameter channels: Int = testValuesIn(validChannels),
    ) {
        val opus = Opus()

        val res = opus.initDecoder(sampleRate, channels)

        assert(res == OPUS_OK)
    }

    data class InitArguments(val sampleRate: Int, val channels: Int)

    @Test
    fun initDecoder_InvalidArgs_ReturnsError(
        @TestParameter initArgs: InitArguments = testValues(
            InitArguments(4_000, 1),
            InitArguments(96_000, 2),
            InitArguments(8_000, 0),
            InitArguments(48_000, 6),
        )
    ) {
        val opus = Opus()

        val res = opus.initDecoder(initArgs.sampleRate, initArgs.channels)

        assert(res != OPUS_OK)
    }

    @Test
    fun releaseDecoder_RunsSuccessfully() {
        val opus = Opus()

        opus.initDecoder(48_000, 2)
        opus.releaseDecoder()
    }

    @Test
    fun getErrorString_ReturnsNonBlankString() {
        val opus = Opus()

        val actual = opus.getErrorString(OPUS_BUFFER_TOO_SMALL)

        assert(actual.isNotBlank())
    }

    /**
     * Table-of-contents (TOC) byte for FB (48 KHz), stereo, 20 ms per frame, single frame packet
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6716#section-3.1">rfc6716 section 3.1</a>
     */
    private val header: Byte = 252.toByte()
    private val opusData = ByteArray(1000) { it.toByte() /* Add bogus signal */ }
        .also { it[0] = header }

    /**
     * Maximum packet frames (120ms, 48kHz)
     */
    private val outputFrames = 5760
    private val samplingRate = 48_000
    private val channels = 2

    /**
     * Integer output signal buffer
     * Size = 5760 (maximum packet frames) * 2 (channels) * 2 (sizeof(int16))
     */
    private val output = ByteArray(outputFrames * channels * 2)

    /**
     * Float output signal buffer
     * Size = 5760 (maximum packet frames) * 2 (channels) * 4 (sizeof(float))
     */
    private val outputFloat = FloatArray(outputFrames * channels * 4)

    @Test
    fun decode_RunsSuccessfully() {
        val opus = Opus()
        opus.initDecoder(samplingRate, channels)

        val framesDecoded = opus.decode(opusData, 320, output, outputFrames, 0)

        assert(framesDecoded > 0)
    }

    @Test
    fun plc_RunsSuccessfully() {
        val opus = Opus()
        opus.initDecoder(samplingRate, channels)
        val frames = opus.decode(opusData, 320, output, outputFrames, 0)

        val framesDecoded = opus.plc(output, frames)

        assert(framesDecoded > 0)
    }

    @Test
    fun decodeFloat_RunsSuccessfully() {
        val opus = Opus()
        opus.initDecoder(samplingRate, channels)

        val framesDecoded = opus.decodeFloat(opusData, 320, outputFloat, outputFrames, 0)

        assert(framesDecoded > 0)
    }

    @Test
    fun plcFloat_RunsSuccessfully() {
        val opus = Opus()
        opus.initDecoder(samplingRate, channels)
        val frames = opus.decodeFloat(opusData, 320, outputFloat, outputFrames, 0)

        val framesDecoded = opus.plcFloat(outputFloat, frames)

        assert(framesDecoded > 0)
    }
}
