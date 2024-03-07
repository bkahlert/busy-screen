
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
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven") }
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
                implementation(platform("com.bkahlert.kommons:kommons-bom:2.8.0"))
                implementation("com.bkahlert.kommons:kommons-time")

                implementation(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.7.1"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

                implementation(platform("org.jetbrains.kotlinx:kotlinx-serialization-bom:1.5.1"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))

                implementation(platform("io.kotest:kotest-bom:5.6.2"))
                implementation("io.kotest:kotest-common")
                implementation("io.kotest:kotest-assertions-core")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
            }
        }

        val jsMain by getting {
            dependencies {
                implementation("com.bkahlert.kommons:kommons-text")
                implementation("com.bkahlert.kommons:kommons-uri")


                implementation("io.ktor:ktor-client-core:1.6.2") { because("websocket") }
                implementation("io.ktor:ktor-client-js:1.6.2") { because("websocket") }
                implementation("io.ktor:ktor-client-websockets:1.6.2") { because("websocket") }

                // https://github.com/JetBrains/kotlin-wrappers
                fun kotlinWrapper(target: String): String = "org.jetbrains.kotlin-wrappers:kotlin-$target"
                val kotlinWrappersVersion = "0.0.1-pre.222-kotlin-1.5.21"
                implementation(enforcedPlatform(kotlinWrapper("wrappers-bom:${kotlinWrappersVersion}")))
                implementation(kotlinWrapper("extensions")) { because("require") }
                implementation(kotlinWrapper("css")) { because("CSSBuilder") }

                implementation("org.jetbrains.kotlinx:kotlinx-html:0.7.3") { because("HTML builder") }
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1") { because("backend polling") }
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2") { because("Status deserialization") }
                implementation("com.soywiz.korlibs.krypto:krypto:2.3.1") { because("MD5") }

                implementation(npm("nes.css", ">= 2.3.0")) { because("retro CSS") }
                implementation(npm("dialog-polyfill", ">= 0.5.6")) { because("help dialog") }


                // tailwind
                implementation(npm("tailwindcss", "^3.3.3")) { because("low-level CSS classes") }

                // optional tailwind plugins
                implementation(devNpm("@tailwindcss/typography", "^0.5")) { because("prose classes to format arbitrary text") }
                implementation(devNpm("tailwind-heropatterns", "^0.0.8")) { because("hero-pattern like striped backgrounds") }

                // webpack
                implementation(devNpm("postcss", "^8.4.17")) { because("CSS post transformation, e.g. auto-prefixing") }
                implementation(devNpm("postcss-loader", "^7.0.1")) { because("Loader to process CSS with PostCSS") }
                implementation(devNpm("postcss-import", "^15.1")) { because("@import support") }
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

tasks.withType<Test> {
    useJUnitPlatform()
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
