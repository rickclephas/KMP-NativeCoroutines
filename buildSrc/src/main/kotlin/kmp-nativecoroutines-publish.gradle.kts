plugins {
    `maven-publish`
    signing
}

ext["signing.keyId"] = null
ext["signing.password"] = null
ext["signing.secretKey"] = null
ext["signing.secretKeyRingFile"] = null
ext["ossrhUsername"] = null
ext["ossrhPassword"] = null
ext["gradle.publish.key"] = null
ext["gradle.publish.secret"] = null
val localPropsFile = project.rootProject.file("local.properties")
if (localPropsFile.exists()) {
    localPropsFile.reader()
        .use { java.util.Properties().apply { load(it) } }
        .onEach { (name, value) -> ext[name.toString()] = value }
} else {
    ext["signing.keyId"] = System.getenv("SIGNING_KEY_ID")
    ext["signing.password"] = System.getenv("SIGNING_PASSWORD")
    ext["signing.secretKey"] = System.getenv("SIGNING_SECRET_KEY")
    ext["signing.secretKeyRingFile"] = System.getenv("SIGNING_SECRET_KEY_RING_FILE")
    ext["ossrhUsername"] = System.getenv("OSSRH_USERNAME")
    ext["ossrhPassword"] = System.getenv("OSSRH_PASSWORD")
    ext["gradle.publish.key"] = System.getenv("GRADLE_PUBLISH_KEY")
    ext["gradle.publish.secret"] = System.getenv("GRADLE_PUBLISH_SECRET")
}

val emptyJavadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

fun getExtraString(name: String) = ext[name]?.toString()

publishing {
    repositories {
        maven {
            name = "sonatype"
            setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = getExtraString("ossrhUsername")
                password = getExtraString("ossrhPassword")
            }
        }
    }

    publications.withType<MavenPublication> {
        artifact(emptyJavadocJar.get())

        pom {
            name.set("KMP-NativeCoroutines")
            description.set("Swift library for Kotlin Coroutines")
            url.set("https://github.com/rickclephas/KMP-NativeCoroutines")
            licenses {
                license {
                    name.set("MIT")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }
            developers {
                developer {
                    id.set("rickclephas")
                    name.set("Rick Clephas")
                    email.set("rclephas@gmail.com")
                }
            }
            scm {
                url.set("https://github.com/rickclephas/KMP-NativeCoroutines")
            }
        }
    }
}

signing {
    getExtraString("signing.secretKey")?.let { secretKey ->
        useInMemoryPgpKeys(getExtraString("signing.keyId"), secretKey, getExtraString("signing.password"))
    }
    sign(publishing.publications)
}