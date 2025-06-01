@file:Suppress("UnstableApiUsage")

plugins {
    `java-gradle-plugin`
    id("kmp-nativecoroutines-kotlin-jvm")
    alias(libs.plugins.gradle.plugin.publish)
    id("kmp-nativecoroutines-publish")
}

kotlin {
    explicitApi()
    jvmToolchain(11)
}

java {
    withSourcesJar()
}

val copyVersionTemplate by tasks.registering(Copy::class) {
    inputs.property("version", version)
    from(layout.projectDirectory.file("Version.kt"))
    into(layout.buildDirectory.dir("generated/kmp-nativecoroutines-version/main"))
    expand("version" to "$version")
    filteringCharset = "UTF-8"
}

tasks.compileKotlin {
    dependsOn(copyVersionTemplate)
}

val sourcesJar by tasks.getting(Jar::class) {
    dependsOn(copyVersionTemplate)
}

sourceSets {
    main {
        java.srcDir(layout.buildDirectory.dir("generated/kmp-nativecoroutines-version/main"))
    }
}

gradlePlugin {
    website = "https://github.com/rickclephas/KMP-NativeCoroutines"
    vcsUrl = "https://github.com/rickclephas/KMP-NativeCoroutines"
    plugins {
        create("kmpNativeCoroutines") {
            id = "com.rickclephas.kmp.nativecoroutines"
            displayName = "KMP-NativeCoroutines"
            description = "Swift library for Kotlin Coroutines"
            implementationClass = "com.rickclephas.kmp.nativecoroutines.gradle.KmpNativeCoroutinesPlugin"
            tags = listOf("kotlin", "swift", "native", "coroutines")
        }
    }
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.ksp.gradle.plugin)
}
