pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "kmp-nativecoroutines"

include(":kmp-nativecoroutines-core")
include(":kmp-nativecoroutines-annotations")
include(":kmp-nativecoroutines-compiler")
include(":kmp-nativecoroutines-gradle-plugin")
include(":sample:shared")