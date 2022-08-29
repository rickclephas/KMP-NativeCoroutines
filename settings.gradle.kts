pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
    }
}

rootProject.name = "kmp-nativecoroutines"

include(":kmp-nativecoroutines-core")
include(":kmp-nativecoroutines-annotations")
include(":kmp-nativecoroutines-compiler")
include(":kmp-nativecoroutines-compiler-embeddable")
include(":kmp-nativecoroutines-gradle-plugin")
include(":kmp-nativecoroutines-ksp")
