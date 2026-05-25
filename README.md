# JOpus

Android library wrapper of the [Opus](https://github.com/xiph/opus) codec.
Decoding to fixed-point and floating-point signal is supported.

## Installation

JOpus is distributed using [JitPack](https://jitpack.io/#ashipo/jopus).

Add the JitPack repository to your `settings.gradle.kts`:

```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // Rest of the repositories
        maven {
            url = uri("https://jitpack.io")
            // Optionally exclude the dependency search from other repositories for security and performance reasons.
            content { includeGroup("com.github.ashipo") }
        }
    }
}
```

Add the dependency:

```
dependencies {
    implementation("com.github.ashipo:jopus:Version")
}
```

## Usage

```kt
val opus = Opus()

// Returns OPUS_OK on success or an error code
val initResult = opus.initDecoder(48000, 2)
if (initResult != OPUS_OK) {
    val errorString = opus.getErrorString(initResult)
    // Handle initialization error
}

// Returns the number of frames(samples per channel) decoded from the packet.
// If that value is negative, then an error has occurred.
val framesDecodedOrError = opus.decode(encodedData, encodedBytes, outputBuffer, outputBufferFrames, fec)
if (framesDecodedOrError < 0) {
    val errorString = opus.getErrorString(framesDecodedOrError)
    // Handle decoding error
}

opus.releaseDecoder()
```

## Documentation

[Opus documentation](https://opus-codec.org/docs/)
