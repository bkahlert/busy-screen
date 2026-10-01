import org.gradle.kotlin.dsl.support.listFilesOrdered
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnLockMismatchReport
import org.jetbrains.kotlin.gradle.targets.js.yarn.yarn

plugins {
    kotlin("multiplatform") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
}

group = "com.bkahlert.busy-screen"
version = "1.1"

repositories {
    mavenCentral()
}

kotlin {
    js(IR) {
        moduleName = "busy-screen"
        browser {
            commonWebpackConfig(Action<KotlinWebpackConfig> {
                devServer = devServer?.copy(open = false)
            })
        }
        yarn.apply {
            ignoreScripts = false // suppress "warning Ignored scripts due to flag." warning
            yarnLockMismatchReport = YarnLockMismatchReport.NONE
            reportNewYarnLock = true // true
            yarnLockAutoReplace = true // true
        }
    }.binaries.executable()

    sourceSets {

        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")
            }
        }

        val jsMain by getting {
            dependencies {
                val ktorVersion = "2.2.3"
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-js:$ktorVersion")
                implementation("io.ktor:ktor-client-websockets:$ktorVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-html:0.7.3") { because("HTML builder") }
                implementation("com.soywiz.korlibs.krypto:krypto:2.3.1") { because("MD5 for Gravatar") }

                implementation(npm("nes.css", ">= 2.3.0")) { because("retro CSS") }
                implementation(npm("dialog-polyfill", ">= 0.5.6")) { because("help dialog") }

                // webpack
                implementation(devNpm("postcss", "^8.4.17")) { because("CSS post transformation, e.g. auto-prefixing") }
                implementation(devNpm("postcss-loader", "^7.0.1")) { because("Loader to process CSS with PostCSS") }
                implementation(devNpm("autoprefixer", "10.4.12")) { because("auto-prefixing by PostCSS") }
                implementation(devNpm("css-loader", "6.7.1"))
                implementation(devNpm("style-loader", "3.3.1"))
                implementation(devNpm("cssnano", "5.1.13")) { because("CSS minification by PostCSS") }
            }
        }
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlin.ExperimentalStdlibApi")
            languageSettings.optIn("kotlin.ExperimentalUnsignedTypes")
            languageSettings.optIn("kotlin.io.encoding.ExperimentalEncodingApi")
            languageSettings.optIn("kotlin.time.ExperimentalTime")
            languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
            languageSettings.optIn("kotlinx.coroutines.FlowPreview")
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}

tasks {
    val removalPattern = listOf(
        Regex("\\.(json)\$"),
        Regex("\\.(jpe?g|png|gif|svg)\$"),
        Regex("\\.(woff|woff2|eot|ttf|otf)\$"),
        Regex("mqtt(\\.min)?\\.js\$"),
        Regex("\\.(css)\$"),
    )

    val removalFilter: (File) -> Boolean = { file ->
        removalPattern.any { it.containsMatchIn(file.name) }
    }

    val productionBuilds = withType<KotlinWebpack>().matching { it.name.endsWith("ProductionWebpack") }
    val cleanUpProductionBuild by registering(Delete::class) {
        mustRunAfter(productionBuilds)
        doLast {
            productionBuilds
                .flatMap { task -> task.outputs.files.filter { it.isDirectory } }
                .forEach { distDir -> distDir.listFilesOrdered(removalFilter).forEach { it.delete() } }
        }
    }
    productionBuilds.configureEach { finalizedBy(cleanUpProductionBuild) }
}
