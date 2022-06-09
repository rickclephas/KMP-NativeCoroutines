plugins {
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.kotlin.jvm)
    `kmp-nativecoroutines-publish`
}

dependencies {
    compileOnly(libs.kotlin.compiler)
}

tasks.compileKotlin.configure {
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
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