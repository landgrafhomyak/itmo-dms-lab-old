import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.js.KotlinJsTarget
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import kotlin.reflect.KProperty

class PropertyByName(private val name: String) {
    private inner class PropertyWrapper(
        private val original: KProperty<*>,
    ) : KProperty<Any?> by original {
        override val name: String
            get() = this@PropertyByName.name
    }

    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): PropertyDelegate =
        project.provideDelegate(thisRef, this.PropertyWrapper(property))
}

fun property(name: String) = PropertyByName(name)

val datetimeVersion: String by property("datetime.version")
val coroutinesVersion: String by property("coroutines.version")
val serializationVersion: String by property("serialization.version")
val ktorVersion: String by property("ktor.version")

plugins {
    kotlin("multiplatform") version "1.6.21"
    kotlin("plugin.serialization") version "1.6.21"
    `maven-publish`
    signing
//    id("com.android.library")
//    id("org.jetbrains.kotlin.android")
}

@Suppress("SpellCheckingInspection")
group = "io.github.landgrafhomyak.itmo"
version = "1.0-b0+-SNAPSHOT"

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

kotlin {
    explicitApi()

    jvm()
    js(IR)
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
            is KotlinJsIrTarget   -> {
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
                implementation(kotlin("stdlib"))
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:$datetimeVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$serializationVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$serializationVersion")
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

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

publishing {
    repositories {
        maven {
            name = "MavenCentralRelease"
            setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = System.getenv("OSSRH_USERNAME")
                password = System.getenv("OSSRH_PASSWORD")
            }
        }
        maven {
            name = "MavenCentralSnapshots"
            setUrl("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            credentials {
                username = System.getenv("OSSRH_USERNAME")
                password = System.getenv("OSSRH_PASSWORD")
            }
        }
    }

    publications.withType<MavenPublication> {

        artifact(javadocJar.get())

        pom {
            name.set("Framework для лабораторных работ ИТМО")
            description.set("Framework для лабораторных работ 2 семестра направления ПИ факультета ПИИКТ университета ИТМО")
            url.set("https://github.com/landgrafhomyak/itmo-dms-lab/tree/core-master")

            licenses {
                license {
                    name.set("CC BY-NC 4.0")
                    url.set("https://creativecommons.org/licenses/by-nc/4.0/")
                }
            }
            developers {
                developer {
                    id.set("landgrafhomyak")
                    name.set("Andrew Golovashevich")
                    email.set("tankist.scratch.p@gmail.com")
                }
            }

            scm {
                url.set("https://github.com/landgrafhomyak/itmo-dms-lab")
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(System.getenv("GPG_SIGNING_KEY"), System.getenv("GPG_SIGNING_PASSWORD"))
    sign(publishing.publications)
}