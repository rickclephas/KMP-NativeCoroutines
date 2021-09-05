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
    version = "0.6.0"

    repositories {
        mavenLocal()
        mavenCentral()
    }
}
