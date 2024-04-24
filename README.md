# JOpus

Android library wrapper of the [Opus](https://github.com/xiph/opus) codec with limited capabilites.
Only fixed-point decoding is supported.

## Usage

```kt
val opus = Opus()
val initResult = opus.initDecoder(48000, 2)
val decodeResult = opus.decode(...)
opus.releaseDecoder()
```

## Documentation

[Opus documentation](https://opus-codec.org/docs/)

### initDecoder

`initDecoder(sampleRate: Int, numChannels: Int): Int`

Initiates the decoder.

`sampleRate` - Sample rate to decode at (Hz). This must be one of 8000, 12000, 16000, 24000, or 48000

`numChannels` - Number of channels (1 or 2) to decode

<b>Returns</b> - `OPUS_OK` on success or [Error codes](https://opus-codec.org/docs/opus_api-1.5/group__opus__errorcodes.html)

See Also: [opus_decoder_create](https://opus-codec.org/docs/opus_api-1.5/group__opus__decoder.html#ga753f6fe0b699c81cfd47d70c8e15a0bd)

### decode

`decode(encodedData: ByteArray, encodedLength: Int, decodedData: ByteArray, frameSize: Int, fec: Int): Int`

Decodes an Opus packet.

`encodedData` - Input payload

`encodedLength` - Number of bytes in payload

`decodedData` - Output signal (PCM 16 bit per sample)

`frameSize` - Number of samples per channel of available space in `decodedData`. Maximum is 5760 (120ms, 48kHz).

`fec` - FEC flag

<b>Returns</b> - Number of decoded samples or [Error codes](https://opus-codec.org/docs/opus_api-1.5/group__opus__errorcodes.html)

See Also: [opus_decode](https://opus-codec.org/docs/opus_api-1.5/group__opus__decoder.html#ga7d1111f64c36027ddcb81799df9b3fc9)

### plc

`plc(decodedData: ByteArray, decodedFrames: Int, fec: Int): Int`

Opus packet loss concealment (PLC) implementation.

`decodedData` - Output signal (PCM 16 bit per sample)

`frameSize` - Number of samples per channel of available space in `decodedData`. Needs to be exactly the duration of audio that is missing, otherwise the decoder will not be in the optimal state to decode the next incoming packet. Also must be a multiple of 2.5 ms.

`fec` - FEC flag

<b>Returns</b> - Number of decoded samples or [Error codes](https://opus-codec.org/docs/opus_api-1.5/group__opus__errorcodes.html)

See Also: [opus_decode](https://opus-codec.org/docs/opus_api-1.5/group__opus__decoder.html#ga7d1111f64c36027ddcb81799df9b3fc9)

### releaseDecoder

`releaseDecoder()`

Destroys the decoder.

### strerror

`strerror(error: Int): String`

Converts an opus error code into a human readable string.

`error` - Error number

<b>Returns</b> - Error string

See Also: [opus_strerror](https://opus-codec.org/docs/opus_api-1.5/group__opus__libinfo.html#gafad3bac5a05dc7c3477a5765eb5e1873)

### Error codes

Library exposes `OPUS_OK` and Opus [error codes](https://opus-codec.org/docs/opus_api-1.5/group__opus__errorcodes.html).

