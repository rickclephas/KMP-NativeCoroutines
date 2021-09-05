buildscript {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
    }
    dependencies {
        classpath(Dependencies.Kotlin.gradlePlugin)
    }
}

allprojects {
    group = "com.rickclephas.kmp"
    version = "0.5.0-kotlin-1.5.20"

    repositories {
        mavenLocal()
        mavenCentral()
    }
}
