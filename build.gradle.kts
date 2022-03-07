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

    implementation("io.ktor:ktor-client-core:1.6.2") { because("websocket") }
    implementation("io.ktor:ktor-client-js:1.6.2") { because("websocket") }
    implementation("io.ktor:ktor-client-websockets:1.6.2") { because("websocket") }

    // https://github.com/JetBrains/kotlin-wrappers
    fun kotlinWrapper(target: String): String = "org.jetbrains.kotlin-wrappers:kotlin-$target"
    val kotlinWrappersVersion = "0.0.1-pre.222-kotlin-1.5.21"
    implementation(enforcedPlatform(kotlinWrapper("wrappers-bom:${kotlinWrappersVersion}")))
    implementation(kotlinWrapper("extensions")) { because("require") }
    implementation(kotlinWrapper("css")) { because("CSSBuilder") }

    implementation("com.bkahlert.kommons:kommons:1.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-html:0.7.3") { because("HTML builder") }
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1") { because("backend polling") }
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2") { because("Status deserialization") }
    implementation("com.soywiz.korlibs.krypto:krypto:2.6.2") { because("MD5") }

    implementation(npm("nes.css", ">= 2.3.0")) { because("retro CSS") }
    implementation(npm("dialog-polyfill", ">= 0.5.6")) { because("help dialog") }
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

tasks.withType<Test> {
    useJUnitPlatform()
}

kotlin {
    js(IR) {
        moduleName = "busy-screen"
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }
}
