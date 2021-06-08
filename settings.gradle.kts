pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "kmp-nativecoroutines"

include(":kmp-nativecoroutines-core")
include(":sample:shared")