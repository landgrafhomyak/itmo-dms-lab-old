import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.targets.js.KotlinJsTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

@Suppress("PropertyName")
val ktor_version: String by project

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    `maven-publish`
//    id("com.android.library")
//    id("org.jetbrains.kotlin.android")
}

repositories {
    mavenCentral()
    google()
}

kotlin {
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
            is KotlinNativeTarget -> {
                binaries.staticLib(NativeBuildType.values().asList())
            }
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
                compileOnly("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.3")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {}
    }
}
