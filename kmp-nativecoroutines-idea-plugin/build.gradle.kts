@file:Suppress("UnstableApiUsage")

import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.tasks.RunIdeTask

plugins {
    id("java")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.intellij.platform)
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

kotlin {
    explicitApi()
    jvmToolchain(17)
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2024.1")

        bundledPlugins("org.jetbrains.kotlin", "com.intellij.gradle")

        pluginVerifier()
        zipSigner()
        instrumentationTools()
    }
    implementation(project(":kmp-nativecoroutines-compiler"))
}

intellijPlatform {
    buildSearchableOptions = false

    pluginConfiguration {
        id = "com.rickclephas.kmp.nativecoroutines"
        name = "KMP-NativeCoroutines"
        description = """
            Provides IDE support for <a href="https://github.com/rickclephas/KMP-NativeCoroutines">KMP-NativeCoroutines</a>:
            <ul>
                <li>Annotation usage validation</li>
                <li>Exposed coroutines warnings</li>
                <li>Quick fixes to add annotations</li>
            </ul>
            Read the <a href="https://github.com/rickclephas/KMP-NativeCoroutines">documentation</a> to get started.
        """.trimIndent()

        ideaVersion {
            sinceBuild = "241"
            untilBuild = "242.*"
        }

        vendor {
            name = "Rick Clephas"
            email = "rclephas@gmail.com"
        }
    }

    signing {
        certificateChain = System.getenv("IDEA_CERTIFICATE_CHAIN")
        privateKey = System.getenv("IDEA_PRIVATE_KEY")
        password = System.getenv("IDEA_PRIVATE_KEY_PASSWORD")
    }

    publishing {
        token = System.getenv("IDEA_PUBLISH_TOKEN")
        if (listOf("-kotlin-", "-EAP-").any { (version as String).contains(it) }) {
            channels = listOf("eap")
        }
    }

    pluginVerification {
        ides {
            recommended()
            select {
                val platformType = findProperty("verificationPlatformType") as String?
                types = when (platformType) {
                    null -> listOf(
                        IntelliJPlatformType.IntellijIdeaCommunity,
                        IntelliJPlatformType.IntellijIdeaUltimate,
                        IntelliJPlatformType.AndroidStudio,
                    )
                    else -> listOf(IntelliJPlatformType.fromCode(platformType))
                }
            }
        }
    }
}

val runIntelliJCommunity by intellijPlatformTesting.runIde.registering {
    type = IntelliJPlatformType.IntellijIdeaCommunity
}

val runIntelliJUltimate by intellijPlatformTesting.runIde.registering {
    type = IntelliJPlatformType.IntellijIdeaUltimate
}

val runAndroidStudio by intellijPlatformTesting.runIde.registering {
    type = IntelliJPlatformType.AndroidStudio
    version = "2024.1.1.11"
}

tasks.withType(RunIdeTask::class) {
    maxHeapSize = "4g"
}
