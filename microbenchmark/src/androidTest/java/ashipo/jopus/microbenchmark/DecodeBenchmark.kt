package ashipo.jopus.microbenchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import ashipo.jopus.OPUS_OK
import ashipo.jopus.Opus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class DecodeBenchmark {

    /**
     * Maximum packet frames (120ms, 48kHz)
     */
    private val outputFrames = 5760

    /**
     * Output signal buffer
     * Size = 5760 (maximum packet frames) * 2 (channels) * 2 (sizeof(int16))
     */
    private val output = ByteArray(outputFrames * 2 * 2)
    private val samplingRate = 48_000
    private val channels = 2

    /**
     * Table-of-contents (TOC) byte for FB (48 KHz), stereo, 20 ms per frame, single frame packet
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6716#section-3.1">rfc6716 section 3.1</a>
     */
    private val header: Byte = 252.toByte()
    private val opusData = ByteArray(1000) { it.toByte() /* Add bogus signal */ }.also {
        it[0] = header
    }

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun decode() {
        val opus = Opus()
        val initResult = opus.initDecoder(samplingRate, channels)
        assertEquals("Decoder init error: $initResult", OPUS_OK, initResult)
        benchmarkRule.measureRepeated {
            val framesDecoded = opus.decode(opusData, 320, output, outputFrames, 0)

            runWithTimingDisabled {
                assertFalse("Decode error: $framesDecoded", framesDecoded < 0)
            }
        }
        opus.releaseDecoder()
    }

    @Test
    fun plc() {
        // 20ms, 48kHz
        val plcFrames = 960

        val opus = Opus()
        val initResult = opus.initDecoder(samplingRate, channels)
        assertEquals("Decoder init error: $initResult", OPUS_OK, initResult)
        benchmarkRule.measureRepeated {
            runWithTimingDisabled {
                // Decode several non PLC packets in between
                repeat(3) {
                    val framesDecoded = opus.decode(opusData, 320, output, outputFrames, 0)
                    assertFalse("Decode error: $framesDecoded", framesDecoded < 0)
                }
            }

            val framesDecoded = opus.plc(output, plcFrames)

            runWithTimingDisabled {
                assertFalse("PLC error: $framesDecoded", framesDecoded < 0)
            }
        }
        opus.releaseDecoder()
    }
}
