plugins {
    kotlin("multiplatform")
    `maven-publish`
}

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }
    /*
    js("nodejs", BOTH) {
        nodejs {}
    }
    @Suppress("SpellCheckingInspection")
    js("webjs", BOTH) {
        browser {}
    }
    */

    val hostOs = System.getProperty("os.name")

    @Suppress("UNUSED_VARIABLE")
    val nativeTarget = when {
        hostOs == "Mac OS X"         -> macosX64("native")
        hostOs == "Linux"            -> linuxX64("native")
        hostOs.startsWith("Windows") -> mingwX64("native")
        else                         -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }


    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting
        /*
        val nodejsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-io-jvm:0.1.1")
            }
        }
        val nodejsTest by getting

        @Suppress("SpellCheckingInspection")
        val webjsMain by getting

        @Suppress("SpellCheckingInspection")
        val webjsTest by getting
        */
        val nativeMain by getting
        val nativeTest by getting

    }
}
