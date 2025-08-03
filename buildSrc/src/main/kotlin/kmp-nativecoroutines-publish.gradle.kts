@file:Suppress("UnstableApiUsage")

import com.vanniktech.maven.publish.SonatypeHost
import gradle.kotlin.dsl.accessors._8edd1b0c1852f0ac869e9c414c462ba9.mavenPublishing

plugins {
    id("com.vanniktech.maven.publish.base")
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
    configureBasedOnAppliedPlugins()
    pom {
        name = "KMP-NativeCoroutines"
        description = "Swift library for Kotlin Coroutines"
        url = "https://github.com/rickclephas/KMP-NativeCoroutines"
        licenses {
            license {
                name = "MIT"
                url = "https://opensource.org/licenses/MIT"
            }
        }
        developers {
            developer {
                id = "rickclephas"
                name = "Rick Clephas"
                email = "rclephas@gmail.com"
            }
        }
        scm {
            url = "https://github.com/rickclephas/KMP-NativeCoroutines"
        }
    }
}
