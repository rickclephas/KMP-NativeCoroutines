@file:Suppress("UnstableApiUsage")

plugins {
    id("java")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.intellij)
}

kotlin {
    explicitApi()
    jvmToolchain(17)
}

dependencies {
    implementation(project(":kmp-nativecoroutines-compiler"))
}

intellij {
//    version = "2024.1"
    localPath = "/Users/rickclephas/Library/Caches/JetBrains/org.jetbrains.fleet-plugin/fleet-backend-sdk"
    type = "IC"
    plugins = listOf("org.jetbrains.kotlin", "com.intellij.gradle")
}

tasks {
    patchPluginXml {
        sinceBuild = "242"
        untilBuild = "242.*"
    }

    buildSearchableOptions {
        enabled = false
    }

    signPlugin {
        certificateChain = System.getenv("IDEA_CERTIFICATE_CHAIN")
        privateKey = System.getenv("IDEA_PRIVATE_KEY")
        password = System.getenv("IDEA_PRIVATE_KEY_PASSWORD")
    }

    publishPlugin {
        token = System.getenv("IDEA_PUBLISH_TOKEN")
        if (listOf("-kotlin-", "-EAP-").any { (version as String).contains(it) }) {
            channels = listOf("eap")
        }
    }

    runIde {
        maxHeapSize = "4g"
    }
}

val pluginDist by configurations.creating {
    isCanBeResolved = false
}
artifacts {
    add(pluginDist.name, tasks.buildPlugin)
}
