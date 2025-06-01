@file:Suppress("UnstableApiUsage")

import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("com.vanniktech.maven.publish.base")
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.S01)
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
