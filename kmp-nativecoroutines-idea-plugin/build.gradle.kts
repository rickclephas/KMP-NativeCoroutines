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
