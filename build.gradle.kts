import kotlinx.validation.ExperimentalBCVApi
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import org.jetbrains.kotlin.gradle.targets.js.npm.tasks.KotlinNpmInstallTask

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlinx.binary.compatibility.validator)
}

buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

allprojects {
    group = "com.rickclephas.kmp"
    version = "1.0.0-ALPHA-43-kotlin-2.2.0-RC"

    repositories {
        mavenCentral()
    }
}

apiValidation {
    @OptIn(ExperimentalBCVApi::class)
    klib {
        enabled = true
    }
}
