plugins {
    kotlin("multiplatform") version "2.4.20"
    kotlin("plugin.serialization") version "2.4.20"
}

group = "com.bkahlert.busy-screen"
version = "1.1"

repositories {
    mavenCentral()
}

kotlin {
    js {
        outputModuleName = "busy-screen"
        compilerOptions {
            target = "es2015"
        }
        browser {
            commonWebpackConfig {
                devServer = devServer?.copy(open = false)
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.11.0")
        }
        jsMain.dependencies {
            val ktorVersion = "3.6.0"
            implementation("io.ktor:ktor-client-core:$ktorVersion")
            implementation("io.ktor:ktor-client-js:$ktorVersion")
            implementation("io.ktor:ktor-client-websockets:$ktorVersion")

            implementation("org.jetbrains.kotlinx:kotlinx-html:0.12.0") { because("HTML builder") }
            implementation("com.soywiz:korlibs-crypto:6.0.1") { because("MD5 for Gravatar") }

            implementation(npm("nes.css", "^2.3.0")) { because("retro CSS") }
            implementation(npm("dialog-polyfill", "^0.5.6")) { because("help dialog") }

            // webpack
            implementation(devNpm("postcss", "^8.5.6"))
            implementation(devNpm("postcss-loader", "^8.2.1"))
            implementation(devNpm("autoprefixer", "^10.6.1"))
            implementation(devNpm("css-loader", "^7.1.5"))
            implementation(devNpm("style-loader", "^4.0.0"))
            implementation(devNpm("cssnano", "^9.1.2"))
        }
    }
}

tasks.named<Sync>("jsBrowserDistribution") {
    // webpack has bundled the stylesheets; only the resources they reference are served as files.
    exclude("*.css")
}
