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
    version = "0.8.0-kotlin-1.6.0-new-mm"

    repositories {
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-coroutines/maven")
        }
    }
}
