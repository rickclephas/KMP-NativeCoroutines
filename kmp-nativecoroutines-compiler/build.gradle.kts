plugins {
    alias(libs.plugins.kotlin.jvm)
    `kmp-nativecoroutines-publish`
}

sourceSets {
    test {
        java.srcDir("src/test/generated")
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
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

tasks.test {
    inputs.dir("src/testData")
    useJUnitPlatform()
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
