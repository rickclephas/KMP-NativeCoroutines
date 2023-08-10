@file:Suppress("UnstableApiUsage")

plugins {
    id("java")
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.kotlin.jvm)
    id("org.jetbrains.intellij") version "1.15.0"
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":kmp-nativecoroutines-compiler"))
}

intellij {
    version = "2022.2"
    type = "IC"
    plugins = listOf("org.jetbrains.kotlin", "com.intellij.gradle")
}

tasks {
    patchPluginXml {
        sinceBuild = "222"
        untilBuild = "232.*"
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
    }

    runIde {
        maxHeapSize = "4g"
    }
}
