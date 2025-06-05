plugins {
    id("kmp-nativecoroutines-kotlin-jvm")
    id("kmp-nativecoroutines-publish")
}

dependencies {
    compileOnly(libs.kotlin.compiler.embeddable)
}

val syncSources by tasks.registering(Sync::class) {
    from(project(":kmp-nativecoroutines-compiler").files("src/main"))
    into("src/main")
    filter {
        when (it) {
            "import com.intellij.psi.PsiElement" -> "import org.jetbrains.kotlin.com.intellij.psi.PsiElement"
            else -> it
        }
    }
}

kotlin {
    explicitApi()
    jvmToolchain(11)
}

val sourcesJar by tasks.getting(Jar::class) {
    dependsOn(syncSources)
}

tasks.compileKotlin.configure {
    dependsOn(syncSources)
    compilerOptions {
        freeCompilerArgs.add("-Xjvm-default=all")
    }
}

tasks.processResources.configure {
    dependsOn(syncSources)
}

tasks.clean.configure {
    delete("src")
}
