plugins {
    alias(libs.plugins.kotlin.jvm)
    `kmp-nativecoroutines-publish`
}

dependencies {
    compileOnly(libs.kotlin.compiler.embeddable)
}

val syncSources by tasks.registering(Sync::class) {
    from(project(":kmp-nativecoroutines-compiler").files("src/main"))
    into("src/main")
}

tasks.compileKotlin.configure {
    dependsOn(syncSources)
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

tasks.clean.configure {
    delete("src")
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
            artifact(sourcesJar)
        }
    }
}
