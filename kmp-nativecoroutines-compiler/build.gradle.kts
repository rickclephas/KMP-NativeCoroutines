plugins {
    @Suppress("DSL_SCOPE_VIOLATION")
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

tasks.compileKotlin.configure {
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

tasks.compileTestKotlin.configure {
    kotlinOptions {
        jvmTarget = "11"
    }
}

tasks.test {
    inputs.dir("src/testData")
    useJUnitPlatform()
}

val generateTests by tasks.registering(JavaExec::class) {
    doFirst { delete("src/test/generated") }
    classpath = sourceSets.test.get().runtimeClasspath
    mainClass.set("com.rickclephas.kmp.nativecoroutines.compiler.GenerateTestsKt")
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
