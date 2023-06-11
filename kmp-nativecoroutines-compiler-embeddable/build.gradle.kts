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

kotlin {
    jvmToolchain(11)
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.compileKotlin.configure {
    dependsOn(syncSources)
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

tasks.processResources.configure {
    dependsOn(syncSources)
}

tasks.clean.configure {
    delete("src")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
