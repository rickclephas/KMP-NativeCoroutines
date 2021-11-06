buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.0-RC2")
        classpath("com.rickclephas.kmp:kmp-nativecoroutines-gradle-plugin")
    }
}

allprojects {
    repositories {
        mavenCentral()
    }
}
