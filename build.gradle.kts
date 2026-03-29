import kotlinx.validation.ExperimentalBCVApi

plugins {
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
    version = "1.0.2-idea-2026.1-EAP-22158.182"
}

apiValidation {
    @OptIn(ExperimentalBCVApi::class)
    klib {
        enabled = true
    }
}
