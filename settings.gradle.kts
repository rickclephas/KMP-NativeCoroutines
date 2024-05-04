pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://cache-redirector.jetbrains.com/intellij-dependencies")
        maven("https://packages.jetbrains.team/maven/p/teamcity-rest-client/teamcity-rest-client")
        maven("https://download.jetbrains.com/teamcity-repository")
        maven {
            url = uri("https://packages.jetbrains.team/maven/p/fleet-plugins-private-preview/fleet-sdk")
            credentials {
                username = settings.providers.gradleProperty("spaceUsername").orNull
                password = settings.providers.gradleProperty("spacePassword").orNull
            }
        }
    }
}

rootProject.name = "kmp-nativecoroutines"

include(":kmp-nativecoroutines-core")
include(":kmp-nativecoroutines-annotations")
include(":kmp-nativecoroutines-compiler")
include(":kmp-nativecoroutines-compiler-embeddable")
include(":kmp-nativecoroutines-gradle-plugin")
include(":kmp-nativecoroutines-idea-plugin")
include(":kmp-nativecoroutines-ksp")
include(":kmp-nativecoroutines-fleet-plugin")
