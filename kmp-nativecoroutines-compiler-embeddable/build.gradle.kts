plugins {
    kotlin("jvm")
    kotlin("kapt")
    `kmp-nativecoroutines-publish`
}

dependencies {
    compileOnly(Dependencies.Kotlin.embeddableCompiler)
    compileOnly(Dependencies.AutoService.annotations)
    kapt(Dependencies.AutoService.processor)
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).all {
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

val syncSources by tasks.registering(Sync::class) {
    from(project(":kmp-nativecoroutines-compiler").sourceSets.main.get().allSource)
    into("src/main/kotlin")
    filter {
        when (it) {
            "import com.intellij.mock.MockProject" -> "import org.jetbrains.kotlin.com.intellij.mock.MockProject"
            else -> it
        }
    }
}

tasks.compileKotlin.configure {
    dependsOn(syncSources)
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