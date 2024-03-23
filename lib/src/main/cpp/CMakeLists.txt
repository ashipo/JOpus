# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.18.1)

# Declares and names the project.

project("jopus")

set(opus_DIR opus)

include_directories(
        ${opus_DIR}/include
        ${opus_DIR}/celt
        ${opus_DIR}/silk)

set(opus_src_DIR ${opus_DIR}/src)

set(OPUS_SRC
        "${opus_src_DIR}/opus.c"
        "${opus_src_DIR}/opus_decoder.c")

set(celt_src_DIR ${opus_DIR}/celt)

set(CELT_SRC
        "${celt_src_DIR}/bands.c"
        "${celt_src_DIR}/celt.c"
        "${celt_src_DIR}/celt_decoder.c"
        "${celt_src_DIR}/celt_lpc.c"
        "${celt_src_DIR}/cwrs.c"
        "${celt_src_DIR}/entcode.c"
        "${celt_src_DIR}/entdec.c"
        "${celt_src_DIR}/entenc.c"
        "${celt_src_DIR}/kiss_fft.c"
        "${celt_src_DIR}/laplace.c"
        "${celt_src_DIR}/mathops.c"
        "${celt_src_DIR}/mdct.c"
        "${celt_src_DIR}/modes.c"
        "${celt_src_DIR}/pitch.c"
        "${celt_src_DIR}/quant_bands.c"
        "${celt_src_DIR}/rate.c"
        "${celt_src_DIR}/vq.c")

set(silk_src_DIR ${opus_DIR}/silk)

set(SILK_SRC
        "${silk_src_DIR}/bwexpander.c"
        "${silk_src_DIR}/bwexpander_32.c"
        "${silk_src_DIR}/CNG.c"
        "${silk_src_DIR}/code_signs.c"
        "${silk_src_DIR}/dec_API.c"
        "${silk_src_DIR}/decode_core.c"
        "${silk_src_DIR}/decode_frame.c"
        "${silk_src_DIR}/decode_indices.c"
        "${silk_src_DIR}/decode_parameters.c"
        "${silk_src_DIR}/decode_pitch.c"
        "${silk_src_DIR}/decode_pulses.c"
        "${silk_src_DIR}/decoder_set_fs.c"
        "${silk_src_DIR}/gain_quant.c"
        "${silk_src_DIR}/init_decoder.c"
        "${silk_src_DIR}/lin2log.c"
        "${silk_src_DIR}/log2lin.c"
        "${silk_src_DIR}/LPC_analysis_filter.c"
        "${silk_src_DIR}/LPC_fit.c"
        "${silk_src_DIR}/LPC_inv_pred_gain.c"
        "${silk_src_DIR}/NLSF_decode.c"
        "${silk_src_DIR}/NLSF_stabilize.c"
        "${silk_src_DIR}/NLSF_unpack.c"
        "${silk_src_DIR}/NLSF2A.c"
        "${silk_src_DIR}/pitch_est_tables.c"
        "${silk_src_DIR}/PLC.c"
        "${silk_src_DIR}/resampler.c"
        "${silk_src_DIR}/resampler_private_AR2.c"
        "${silk_src_DIR}/resampler_private_down_FIR.c"
        "${silk_src_DIR}/resampler_private_IIR_FIR.c"
        "${silk_src_DIR}/resampler_private_up2_HQ.c"
        "${silk_src_DIR}/resampler_rom.c"
        "${silk_src_DIR}/shell_coder.c"
        "${silk_src_DIR}/sort.c"
        "${silk_src_DIR}/stereo_decode_pred.c"
        "${silk_src_DIR}/stereo_MS_to_LR.c"
        "${silk_src_DIR}/sum_sqr_shift.c"
        "${silk_src_DIR}/table_LSF_cos.c"
        "${silk_src_DIR}/tables_gain.c"
        "${silk_src_DIR}/tables_LTP.c"
        "${silk_src_DIR}/tables_NLSF_CB_NB_MB.c"
        "${silk_src_DIR}/tables_NLSF_CB_WB.c"
        "${silk_src_DIR}/tables_other.c"
        "${silk_src_DIR}/tables_pitch_lag.c"
        "${silk_src_DIR}/tables_pulses_per_block.c")

set(CMAKE_C_FLAGS "-DUSE_ALLOCA -DOPUS_BUILD -DFIXED_POINT=1 -DDISABLE_FLOAT_API \
        -DHAVE_LRINT=1 -DHAVE_LRINTF=1")

add_library(
        opus
        STATIC
        ${OPUS_SRC} ${CELT_SRC} ${SILK_SRC})

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
        jopus

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        jopus.cpp)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        jopus
        opus
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})