plugins {
    id("java")
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.kotlin.jvm)
    id("org.jetbrains.intellij") version "1.15.0"
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
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    patchPluginXml {
        sinceBuild.set("222")
        untilBuild.set("232.*")
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
