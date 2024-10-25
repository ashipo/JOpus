package com.fake.microbenchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fake.jopus.OPUS_BAD_ARG
import com.fake.jopus.OPUS_OK
import com.fake.jopus.Opus
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CommonBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun initDecoder() {
        val opus = Opus()
        benchmarkRule.measureRepeated {
            val initResult = opus.initDecoder(48_000, 2)

            runWithTimingDisabled {
                opus.releaseDecoder()
                assertEquals("Decoder init error: $initResult", OPUS_OK, initResult)
            }
        }
    }

    @Test
    fun releaseDecoder() {
        val opus = Opus()
        benchmarkRule.measureRepeated {
            runWithTimingDisabled {
                val initResult = opus.initDecoder(48_000, 2)
                assertEquals("Decoder init error: $initResult", OPUS_OK, initResult)
            }

            opus.releaseDecoder()
        }
    }

    @Test
    fun getErrorString() {
        val opus = Opus()
        benchmarkRule.measureRepeated {
            opus.getErrorString(OPUS_BAD_ARG)
        }
    }
}
