plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    `maven-publish`
}

kotlin {
    jvmToolchain(17)
}

android {
    namespace = "ashipo.jopus"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }
    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        externalNativeBuild {
            cmake {
                cppFlags += listOf()
            }
        }
    }
    buildTypes {
        release {
            consumerProguardFiles("proguard-rules.pro")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.test.parameter.injector)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.github.ashipo"
            artifactId = "jopus"
            version = "0.3.1"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
