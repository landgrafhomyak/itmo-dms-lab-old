plugins {
    kotlin("multiplatform")
    application
}

repositories {
    mavenCentral()
}

application {
    @Suppress("SpellCheckingInspection")
    mainClass.set("Main")
}

kotlin {
    this.jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
    }

    /* js("nodejs", BOTH) {
        nodejs {}
    } */
    @Suppress("SpellCheckingInspection")
    js("webjs", BOTH) {
        browser {}
    }


    val hostOs = System.getProperty("os.name")

    @Suppress("UNUSED_VARIABLE")
    val nativeTarget = when {
        hostOs == "Mac OS X"         -> macosX64("native")
        hostOs == "Linux"            -> linuxX64("native")
        hostOs.startsWith("Windows") -> mingwX64("native") {
            binaries.executable {}
        }
        else                         -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }


    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
                implementation(project(":common"))
                implementation(project(":interactive:text"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }


        @Suppress("SpellCheckingInspection")
        val webjsMain by getting

        @Suppress("SpellCheckingInspection")
        val webjsTest by getting


        val terminalMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(project(":platform:coroutines"))
            }
        }
        val terminalTest by creating {
            dependsOn(commonTest)
        }

        val jvmMain by getting {
            dependsOn(terminalMain)
        }
        val jvmTest by getting {
            dependsOn(terminalTest)
        }

        /* val nodejsMain by getting {
            dependsOn(terminalMain)
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-io-jvm:0.1.1")
            }
        }
        val nodejsTest by getting {
            dependsOn(terminalTest)
        } */

        val nativeMain by getting {
            dependsOn(terminalMain)
        }
        val nativeTest by getting {
            dependsOn(terminalTest)
        }

    }
}
