buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.20-dev-7170")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.6.20-dev-7170")
        classpath("com.rickclephas.kmp:kmp-nativecoroutines-gradle-plugin")
    }
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
    }
}
