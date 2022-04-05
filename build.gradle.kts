buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    dependencies {
        classpath(Dependencies.Kotlin.gradlePlugin)
    }
}

allprojects {
    group = "com.rickclephas.kmp"
    version = "0.12.1"

    repositories {
        mavenCentral()
    }
}
