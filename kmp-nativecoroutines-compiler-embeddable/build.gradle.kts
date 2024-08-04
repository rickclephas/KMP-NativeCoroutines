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
    filter {
        when (it) {
            "import com.intellij.psi.PsiElement" -> "import org.jetbrains.kotlin.com.intellij.psi.PsiElement"
            "import com.intellij.openapi.util.TextRange" -> "import org.jetbrains.kotlin.com.intellij.openapi.util.TextRange"
            "import com.intellij.lang.LighterASTNode" -> "import org.jetbrains.kotlin.com.intellij.lang.LighterASTNode"
            "import com.intellij.openapi.util.Ref" -> "import org.jetbrains.kotlin.com.intellij.openapi.util.Ref"
            "import com.intellij.util.diff.FlyweightCapableTreeStructure" -> "import org.jetbrains.kotlin.com.intellij.util.diff.FlyweightCapableTreeStructure"
            else -> it
        }
    }
}

kotlin {
    explicitApi()
    jvmToolchain(11)
}

java {
    withJavadocJar()
    withSourcesJar()
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
