plugins {
    kotlin("jvm")
    kotlin("kapt")
    `kmp-nativecoroutines-publish`
}

dependencies {
    compileOnly(Dependencies.Kotlin.compiler)
    compileOnly(Dependencies.AutoService.annotations)
    kapt(Dependencies.AutoService.processor)
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).all {
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
        }
    }
}