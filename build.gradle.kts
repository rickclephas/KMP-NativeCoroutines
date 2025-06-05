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
    version = "1.0.0-ALPHA-44"
}

apiValidation {
    @OptIn(ExperimentalBCVApi::class)
    klib {
        enabled = true
    }
}
