plugins {
    alias(libs.plugins.kotlin.jvm)
    id("org.jetbrains.fleet-plugin") version "0.4.198"
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
        version = "1.35.40"
    }

    backendRequirements {
        intellij {
            plugin(project(":kmp-nativecoroutines-idea-plugin", "pluginDist"))
        }
    }
}
