import org.jetbrains.compose.compose

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "com.example"
version = "1.0-SNAPSHOT"

kotlin {
    jvm("desktop") {
    }
    wasm {
        browser()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
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

        val wasmMain by getting {
            dependsOn(nonAndroidMain)
        }
    }
}

compose.experimental {
    web.application {}
}

//compose {
//    val composeVersion = libs.versions.wasm.compose.get()
//    kotlinCompilerPlugin.set(composeVersion)
//    val kotlinVersion = libs.versions.kotlin.asProvider().get()
//    kotlinCompilerPluginArgs.add("suppressKotlinVersionCompatibilityCheck=$kotlinVersion")
//}
