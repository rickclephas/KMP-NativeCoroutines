import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinUsages
import org.jetbrains.kotlin.gradle.utils.NativeCompilerDownloader
import org.jetbrains.kotlin.konan.target.HostManager

plugins {
    alias(libs.plugins.kotlin.jvm)
    `kmp-nativecoroutines-publish`
}

sourceSets {
    test {
        java.srcDir("src/test/generated")
    }
}

val nativeTestClasspath by configurations.creating {
    attributes {
        attribute(KotlinPlatformType.attribute, KotlinPlatformType.native)
        attribute(KotlinNativeTarget.konanTargetAttribute, HostManager.hostName)
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(KotlinUsages.KOTLIN_API))
    }
}

dependencies {
    compileOnly(libs.kotlin.compiler)
    testImplementation(libs.kotlin.compiler)
    testImplementation(libs.kotlin.compiler.internalTestFramework)
    testImplementation(libs.kotlin.reflect)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.platform.commons)
    testImplementation(libs.junit.platform.launcher)
    testRuntimeOnly(libs.junit)

    nativeTestClasspath(project(":kmp-nativecoroutines-annotations"))
    nativeTestClasspath(project(":kmp-nativecoroutines-core"))
}

kotlin {
    explicitApi()
    jvmToolchain(11)
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.compileKotlin.configure {
    compilerOptions {
        freeCompilerArgs.add("-Xjvm-default=all")
    }
}

tasks.test {
    dependsOn(nativeTestClasspath)

    inputs.dir("src/testData")
    useJUnitPlatform()

    // TODO: Remove workaround for https://youtrack.jetbrains.com/issue/KT-66929
    val nativeCompilerDir =  NativeCompilerDownloader(project).apply { downloadIfNeeded() }.compilerDirectory
    systemProperty("kotlin.internal.native.test.nativeHome", nativeCompilerDir.absolutePath)

    systemProperty("com.rickclephas.kmp.nativecoroutines.test.classpath-native", nativeTestClasspath.asPath)
}

val deleteGeneratedTests by tasks.registering(Delete::class) {
    delete("src/test/generated")
}

val generateTests by tasks.registering(JavaExec::class) {
    dependsOn(deleteGeneratedTests)
    classpath = sourceSets.test.get().runtimeClasspath
    mainClass.set("com.rickclephas.kmp.nativecoroutines.compiler.GenerateTestsKt")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
