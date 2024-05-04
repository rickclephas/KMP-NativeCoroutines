plugins {
    alias(libs.plugins.kotlin.jvm)
    id("org.jetbrains.fleet-plugin") version "0.4.215"
}

repositories {
    mavenCentral()
    maven("https://cache-redirector.jetbrains.com/intellij-dependencies")
}

fleetPlugin {
    id = "com.rickclephas.kmp.nativecoroutines"

    metadata {
        readableName = "KMP-NativeCoroutines"
        description = "Swift library for Kotlin Coroutines"
    }

    fleetRuntime {
        version = "1.37.85"
    }

    backendRequirements {
        intellij {
            plugin(project(":kmp-nativecoroutines-idea-plugin", "intellijPlatformDistribution"))
        }
    }
}
