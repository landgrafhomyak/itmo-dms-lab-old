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

//    js(BOTH) {
//        nodejs {}
//        browser {}
//    }

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
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.1")
            }
        }
        val nativeMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-native:1.3.0")
            }
        }
//        val jsMain by getting {
//            dependencies {
//                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.3.0")
//            }
//        }

    }
}
