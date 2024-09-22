@file:Suppress("UnstableApiUsage")

import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.tasks.RunIdeTask
import org.jetbrains.intellij.platform.gradle.tasks.VerifyPluginTask

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
    jvmToolchain(21)
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2024.2.1")

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
            sinceBuild = "242"
            untilBuild = "243.*"
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
            val verificationIde = findProperty("verificationIde") as String?
            if (verificationIde != null) {
                val (platformType, build) = verificationIde.split('-', limit = 2)
                select {
                    types = listOf(IntelliJPlatformType.fromCode(platformType))
                    sinceBuild = build
                    untilBuild = "$build.*"
                }
            } else {
                recommended()
                select {
                    types = listOf(
                        IntelliJPlatformType.IntellijIdeaCommunity,
                        IntelliJPlatformType.IntellijIdeaUltimate,
                        IntelliJPlatformType.AndroidStudio,
                    )
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
    version = "2024.2.1.7"
}

tasks.withType(RunIdeTask::class) {
    maxHeapSize = "4g"
    jvmArguments.add("-Didea.kotlin.plugin.use.k2=true")
    jvmArguments.add("-Dkotlin.k2.only.bundled.compiler.plugins.enabled=false")
}

tasks.withType(VerifyPluginTask::class) {
    val verificationIde = findProperty("verificationIde") as String? ?: return@withType
    onlyIf {
        val hasIdes = ides.files.isNotEmpty()
        if (!hasIdes) logger.warn("::warning title=Skipped unknown IDE::Unknown IDE $verificationIde")
        hasIdes
    }
}
