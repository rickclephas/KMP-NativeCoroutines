@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://redirector.kotlinlang.org/maven/dev")
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://redirector.kotlinlang.org/maven/dev")
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
