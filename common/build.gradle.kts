plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "com.example"
version = "1.0-SNAPSHOT"

kotlin {
    jvm("desktop") {
    }
    /* without WASM
    wasm {
        browser()
    }
    */
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                implementation("co.touchlab:kermit:${libs.versions.kermit.v()}")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:${libs.versions.kotlinx.datetime.v()}")
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
        val wasmMain by getting {
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
    //kotlinCompilerPlugin.set(libs.versions.compose.asProvider().get())
    kotlinCompilerPlugin.set(libs.versions.compose.compiler.get())
    kotlinCompilerPluginArgs.add("suppressKotlinVersionCompatibilityCheck=${libs.versions.kotlin.asProvider().get()}")
}
