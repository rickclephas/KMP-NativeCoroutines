plugins {
    `java-gradle-plugin`
    kotlin("jvm")
    kotlin("kapt")
    `kmp-nativecoroutines-publish`
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

gradlePlugin {
    plugins {
        create("kmpNativeCoroutines") {
            displayName = "KMP-NativeCoroutines"
            description = "Swift library for Kotlin Coroutines"
            id = "com.rickclephas.kmp.nativecoroutines"
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