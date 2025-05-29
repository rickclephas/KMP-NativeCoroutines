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
val jvmTestClasspath by configurations.creating {
    attributes {
        attribute(KotlinPlatformType.attribute, KotlinPlatformType.jvm)
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

    testRuntimeOnly(libs.kotlin.test)
    testRuntimeOnly(libs.kotlin.script.runtime)
    testRuntimeOnly(libs.kotlin.annotations.jvm)

    nativeTestClasspath(project(":kmp-nativecoroutines-annotations"))
    nativeTestClasspath(project(":kmp-nativecoroutines-core"))
    jvmTestClasspath(project(":kmp-nativecoroutines-annotations"))
    jvmTestClasspath(project(":kmp-nativecoroutines-core"))
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

val requireCompilerTestsBuild by requireBuildType(BuildType.COMPILER_TESTS)

tasks.test {
    dependsOn(requireCompilerTestsBuild)
    dependsOn(nativeTestClasspath)
    dependsOn(jvmTestClasspath)

    inputs.dir("src/testData")
    useJUnitPlatform()

    // TODO: Remove workaround for https://youtrack.jetbrains.com/issue/KT-66929
    val nativeCompilerDir =  NativeCompilerDownloader(project).apply { downloadIfNeeded() }.compilerDirectory
    systemProperty("kotlin.internal.native.test.nativeHome", nativeCompilerDir.absolutePath)

    val testRuntimeClasspathFiles = project.configurations.testRuntimeClasspath.map { it.files }
    doFirst {
        listOf(
            "kotlin-stdlib",
            "kotlin-stdlib-jdk8",
            "kotlin-reflect",
            "kotlin-test",
            "kotlin-script-runtime",
            "kotlin-annotations-jvm"
        ).forEach { jarName ->
            val path = testRuntimeClasspathFiles.get().find {
                """$jarName-\d.*jar""".toRegex().matches(it.name)
            }?.absolutePath ?: return@forEach
            systemProperty("org.jetbrains.kotlin.test.$jarName", path)
        }
    }

    systemProperty("com.rickclephas.kmp.nativecoroutines.test.classpath-jvm", jvmTestClasspath.asPath)
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
