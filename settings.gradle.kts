pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
    }
}

rootProject.name = "kmp-nativecoroutines"

include(":kmp-nativecoroutines-core")
include(":kmp-nativecoroutines-annotations")
include(":kmp-nativecoroutines-compiler")
include(":kmp-nativecoroutines-compiler-embeddable")
include(":kmp-nativecoroutines-gradle-plugin")
include(":kmp-nativecoroutines-ksp")
