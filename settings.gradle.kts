@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "kmp-nativecoroutines"

include(":kmp-nativecoroutines-core")
include(":kmp-nativecoroutines-annotations")
include(":kmp-nativecoroutines-compiler")
include(":kmp-nativecoroutines-compiler-embeddable")
include(":kmp-nativecoroutines-gradle-plugin")
include(":kmp-nativecoroutines-idea-plugin")
include(":kmp-nativecoroutines-ksp")
