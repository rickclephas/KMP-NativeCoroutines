buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://redirector.kotlinlang.org/maven/dev")
    }
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://redirector.kotlinlang.org/maven/dev")
    }
}
