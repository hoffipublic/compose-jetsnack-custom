plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "com.example"
version = "1.0-SNAPSHOT"

kotlin {
    jvm("desktop") {
        //withJava()
    }
    /* without WASM
    @OptIn(org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }
    */
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:${libs.versions.kotlinx.datetime.v()}")
                implementation("co.touchlab:kermit:${libs.versions.kermit.v()}")
            }
        }
        val nonAndroidMain by creating {
            dependsOn(commonMain)
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val desktopMain by getting {
            dependsOn(nonAndroidMain)
            dependencies {
                api(compose.preview)
            }
        }
        val desktopTest by getting

        /* without WASM
        val wasmJsMain by getting {
            dependsOn(nonAndroidMain)
        }
        */
    }
}

/* without WASM
compose.experimental {
    web.application {}
}
*/

// if we wanna use special combinations of compose, kotlin and composeCompiler
compose {
    kotlinCompilerPlugin.set(libs.versions.kotlin.compose.compiler.get())
    kotlinCompilerPluginArgs.add("suppressKotlinVersionCompatibilityCheck=${libs.versions.kotlin.asProvider().get()}")
}
