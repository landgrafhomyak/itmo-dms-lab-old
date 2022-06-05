import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.js.KotlinJsTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

@Suppress("PropertyName")
val ktor_version: String by project

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
//    id("com.android.library")
//    id("org.jetbrains.kotlin.android")
}

repositories {
    mavenCentral()
    google()
    maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

kotlin {
    explicitApi()

    jvm()
    js(BOTH)
    ios()
    mingwX64()
    // mingwX86()
    watchos()
    tvos()
    macosArm64()
    linuxX64()
    // linuxMips32()
    // linuxArm32Hfp()
    // linuxMipsel32()
    macosX64()
    // android()
    // wasm()

    targets.all target@{
        when (this@target) {
            is KotlinNativeTarget -> {}
            is KotlinJsTarget     -> {
                nodejs {}
                browser {}
            }
            is KotlinJvmTarget    -> {
                compilations.all {
                    kotlinOptions.jvmTarget = "1.8"
                }
            }
        }
    }

    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.3")
                @Suppress("SpellCheckingInspection")
                implementation("io.github.landgrafhomyak.itmo:dms-lab-core:1.0-b1-SNAPSHOT")

            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val jvmMain by getting {}

        val nativeMain by creating {
            dependsOn(commonMain)
        }

        val nativeTest by creating {
            dependsOn(commonTest)
        }

        val iosMain by getting {
            dependsOn(nativeMain)
        }

        val iosTest by getting {
            dependsOn(nativeTest)
        }

        val mingwX64Main by getting {
            dependsOn(nativeMain)
        }

        val mingwX64Test by getting {
            dependsOn(nativeTest)
        }

        val watchosMain by getting {
            dependsOn(nativeMain)
        }

        val watchosTest by getting {
            dependsOn(nativeTest)
        }
        val tvosMain by getting {
            dependsOn(nativeMain)
        }

        val tvosTest by getting {
            dependsOn(nativeTest)
        }

        val macosArm64Main by getting {
            dependsOn(nativeMain)
        }

        val macosArm64Test by getting {
            dependsOn(nativeTest)
        }

        val linuxX64Main by getting {
            dependsOn(nativeMain)
        }

        val linuxX64Test by getting {
            dependsOn(nativeTest)
        }

        val macosX64Main by getting {
            dependsOn(nativeMain)
        }

        val macosX64Test by getting {
            dependsOn(nativeTest)
        }
    }
}
