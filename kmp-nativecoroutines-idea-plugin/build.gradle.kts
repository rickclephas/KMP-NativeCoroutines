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
    version.set("2022.2")
    type.set("IC")
    plugins.set(listOf("org.jetbrains.kotlin", "com.intellij.gradle"))
}

tasks {
    patchPluginXml {
        sinceBuild.set("222")
        untilBuild.set("232.*")
    }

    buildSearchableOptions {
        enabled = false
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

    runIde {
        maxHeapSize = "4g"
    }
}
