buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.6.10")
        classpath("com.rickclephas.kmp:kmp-nativecoroutines-gradle-plugin")
    }
}

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
    }
}
