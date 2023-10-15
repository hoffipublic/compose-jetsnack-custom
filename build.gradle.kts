group "com.example"
version "1.0-SNAPSHOT"

allprojects {
    configurations.all {
        val conf: Configuration = this
        conf.resolutionStrategy.eachDependency {
            val dependencyResolveDetails: DependencyResolveDetails = this
            val isWasm = conf.name.contains("wasm", true)
            val isJs = conf.name.contains("js", true) && ! conf.name.contains("wasm", true)
            val isComposeGroup = requested.module.group.startsWith("org.jetbrains.compose")
            val isComposeCompiler = requested.module.group.startsWith("org.jetbrains.compose.compiler")
            if (isComposeGroup && !isComposeCompiler && !isWasm && !isJs) {
                val composeVersion = libs.versions.kotlin.compose.asProvider().get()
                dependencyResolveDetails.useVersion(composeVersion)
println("allprojects prj:%-12s: %-10s -> %-35s : %-28s (conf: %-52s)".format("'${project.name}'", composeVersion, "'${requested.module.group}", "${requested.module.name}'", conf.name))
            }
            if (requested.module.name.startsWith("kotlin-stdlib")) {
                //val kotlinVersion = libs.versions.kotlin.compose.kotlin.stdlib.wasm.get()
                val kotlinVersionWasm = libs.versions.kotlin.compose.kotlin.stdlib.wasm.get()
                //println("-> ${dependencyResolveDetails.target}")
                if (requested.module.name == "kotlin-stdlib-wasm-js") {
//                    useTarget("org.jetbrains.kotlin:kotlin-stdlib-wasm:$kotlinVersion")
                    useTarget("org.jetbrains.kotlin:kotlin-stdlib-wasm:$kotlinVersionWasm")
                } else
                dependencyResolveDetails.useVersion(kotlinVersionWasm)
println("!!! %s:%s:%s".format(requested.module.group, requested.module.name, kotlinVersionWasm))
            }
        }
    }
    afterEvaluate {
        println("kotlin plugin:            ${libs.versions.kotlin.asProvider().get()}")
        println("compose version:          ${libs.versions.kotlin.compose.asProvider().get()}")
        println("compose wasm version:     ${libs.versions.kotlin.compose.wasm.get()}")
        println("compose compiler version: ${libs.versions.kotlin.compose.compiler.get()}")
    }
}

plugins {
    //kotlin("jvm") version libs.versions.kotlin.asProvider().get()
    kotlin("multiplatform") version libs.versions.kotlin.asProvider().get() apply false
    //kotlin("android").version(extra["kotlin.version"] as String)
    //id("com.android.application").version(extra["agp.version"] as String)
    //id("com.android.library").version(extra["agp.version"] as String)
    id("org.jetbrains.compose") version libs.versions.kotlin.compose.wasm.get() apply false
    id("buildLogic.binaryPlugins.ProjectSetupBuildLogicPlugin")
    id("buildLogic.binaryPlugins.ProjectInfosBuildLogicPlugin")
    id("VersionsUpgradeBuildLogic")
}
