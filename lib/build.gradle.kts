plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    `maven-publish`
}

kotlin {
    jvmToolchain(8)
}

android {
    namespace = "com.fake.jopus"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        externalNativeBuild {
            cmake {
                cppFlags += listOf()
            }
        }
    }

    buildTypes {
        release {
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
    buildToolsVersion = "34.0.0"
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.github.ashipo"
            artifactId = "jopus"
            version = "0.2.1"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
