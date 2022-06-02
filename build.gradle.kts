plugins {
    kotlin("multiplatform") version "1.6.20" apply false
    kotlin("plugin.serialization") version "1.6.20" apply false
//    id("com.android.library") version "7.0" apply false
    id("org.jetbrains.kotlin.android") version "1.6.20" apply false
}

repositories {
    google()
    mavenCentral()
}

@Suppress("SpellCheckingInspection")
group = "com.github.landgrafhomyak.itmo"
version = "1.0-SNAPSHOT"