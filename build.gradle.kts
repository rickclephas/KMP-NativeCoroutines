//import kotlinx.validation.ExperimentalBCVApi
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import org.jetbrains.kotlin.gradle.targets.js.npm.tasks.KotlinNpmInstallTask

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
//    alias(libs.plugins.kotlinx.binary.compatibility.validator)
}

buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

allprojects {
    group = "com.rickclephas.kmp"
    version = "1.0.0-ALPHA-27-idea-2024.1-EAP-14494.158"

    repositories {
        mavenCentral()
    }
}

//apiValidation {
//    @OptIn(ExperimentalBCVApi::class)
//    klib {
//        enabled = true
//    }
//}

// TODO: Remove once default NodeJS version supports wasm
plugins.withType<NodeJsRootPlugin> {
    extensions.configure(NodeJsRootExtension::class) {
        nodeVersion = "21.0.0-v8-canary20231019bd785be450"
        nodeDownloadBaseUrl = "https://nodejs.org/download/v8-canary"
    }
    tasks.withType<KotlinNpmInstallTask> {
        args.add("--ignore-engines")
    }
}
