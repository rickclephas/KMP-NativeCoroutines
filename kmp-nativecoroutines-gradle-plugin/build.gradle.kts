plugins {
    `java-gradle-plugin`
    kotlin("jvm")
    kotlin("kapt")
    `maven-publish`
}

gradlePlugin {
    plugins {
        create("kmpNativeCoroutines") {
            id = "com.rickclephas.kmp.nativecoroutines"
            implementationClass = "com.rickclephas.kmp.nativecoroutines.gradle.KmpNativeCoroutinesPlugin"
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin-api:1.5.10")
}