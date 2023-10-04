group "com.example"
version "1.0-SNAPSHOT"

allprojects {
    configurations.all {
        val conf = this
        conf.resolutionStrategy.eachDependency {
            val isWasm = conf.name.contains("wasm", true)
            val isJs = conf.name.contains("js", true)
            val isComposeGroup = requested.module.group.startsWith("org.jetbrains.compose")
            val isComposeCompiler = requested.module.group.startsWith("org.jetbrains.compose.compiler")
            if (isComposeGroup && !isComposeCompiler && !isWasm && !isJs) {
                val composeVersion = libs.versions.compose.asProvider().get()
                useVersion(composeVersion)
            }
            if (requested.module.name.startsWith("kotlin-stdlib")) {
                val kotlinVersion = libs.versions.kotlin.asProvider().get()
                useVersion(kotlinVersion)
            }
        }
    }
}

plugins {
    kotlin("jvm") version libs.versions.kotlin.asProvider().get()
    kotlin("multiplatform") version libs.versions.kotlin.asProvider().get() apply false
    //kotlin("android").version(extra["kotlin.version"] as String)
    //id("com.android.application").version(extra["agp.version"] as String)
    //id("com.android.library").version(extra["agp.version"] as String)
    id("org.jetbrains.compose") version libs.versions.compose.asProvider().get() apply false
    id("buildLogic.binaryPlugins.ProjectSetupBuildLogicPlugin")
    id("buildLogic.binaryPlugins.ProjectInfosBuildLogicPlugin")
    id("VersionsUpgradeBuildLogic")
}
