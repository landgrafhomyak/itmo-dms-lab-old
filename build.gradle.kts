plugins {
    kotlin("multiplatform") version "1.6.20" apply false
    kotlin("plugin.serialization") version "1.6.20" apply false
//    id("com.android.library") version "7.0" apply false
    id("org.jetbrains.kotlin.android") version "1.6.20" apply false
}


@Suppress("SpellCheckingInspection")
group = "com.github.landgrafhomyak.itmo"
version = "1.0b0"

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}