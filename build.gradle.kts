import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

plugins {
    kotlin("js") version "1.5.21"
    kotlin("plugin.serialization") version "1.5.21"
}

group = "com.bkahlert"
version = "1.0"

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven") }
}

dependencies {
    testImplementation(kotlin("test"))
    // TODO wait for 5.0 for IR compiler support
//    testImplementation("io.kotest:kotest-framework-engine-js:4.6.1")
//    testImplementation("io.kotest:kotest-assertions-core-js:4.6.1")

    implementation("com.bkahlert:koodies:1.6.0-SNAPSHOT")
    implementation("org.jetbrains.kotlinx:kotlinx-html:0.7.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
    implementation("com.soywiz.korlibs.krypto:krypto:2.2.0") {
        because("MD5")
    }

//    implementation(npm("nes.css", ">= 2.3.0"))
}

tasks.withType<Kotlin2JsCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf(
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xopt-in=kotlin.time.ExperimentalTime",
            "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi",
        )
    }
}

tasks {
    // Tests
    withType<Test> {
        useJUnitPlatform()
    }
}

kotlin {
    js(IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }
}
