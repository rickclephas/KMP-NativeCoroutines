plugins {
    `java-gradle-plugin`
    kotlin("jvm")
    `kmp-nativecoroutines-publish`
    id("com.gradle.plugin-publish") version "0.15.0"
}

val copyVersionTemplate by tasks.registering(Copy::class) {
    inputs.property("version", version)
    from(layout.projectDirectory.file("Version.kt"))
    into(layout.buildDirectory.dir("generated/kmp-nativecoroutines-version/main"))
    expand("version" to "$version")
    filteringCharset = "UTF-8"
}

kotlin.sourceSets.all {
    languageSettings.optIn("kotlin.RequiresOptIn")
}

tasks.compileKotlin {
    dependsOn(copyVersionTemplate)
}

sourceSets {
    main {
        java.srcDir("$buildDir/generated/kmp-nativecoroutines-version/main")
    }
}

pluginBundle {
    website = "https://github.com/rickclephas/KMP-NativeCoroutines"
    vcsUrl = "https://github.com/rickclephas/KMP-NativeCoroutines"
    tags = listOf("kotlin", "swift", "native", "coroutines")
}

gradlePlugin {
    plugins {
        create("kmpNativeCoroutines") {
            id = "com.rickclephas.kmp.nativecoroutines"
            displayName = "KMP-NativeCoroutines"
            description = "Swift library for Kotlin Coroutines"
            implementationClass = "com.rickclephas.kmp.nativecoroutines.gradle.KmpNativeCoroutinesPlugin"
        }
    }
}

dependencies {
    implementation(Dependencies.Kotlin.gradlePlugin)
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

publishing {
    publications.withType<MavenPublication> {
        artifact(sourcesJar)
    }
}