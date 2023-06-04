plugins {
    `java-gradle-plugin`
    alias(libs.plugins.kotlin.jvm)
    `kmp-nativecoroutines-publish`
    alias(libs.plugins.gradle.plugin.publish)
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
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.ksp.gradle.plugin)
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
