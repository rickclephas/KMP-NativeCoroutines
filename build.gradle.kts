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
    version = "0.2.0"

    repositories {
        mavenLocal()
        mavenCentral()
    }

    ext["signing.keyId"] = null
    ext["signing.password"] = null
    ext["signing.secretKeyRingFile"] = null
    ext["ossrhUsername"] = null
    ext["ossrhPassword"] = null
    val localPropsFile = project.rootProject.file("local.properties")
    if (localPropsFile.exists()) {
        localPropsFile.reader()
            .use { java.util.Properties().apply { load(it) } }
            .onEach { (name, value) -> ext[name.toString()] = value }
    } else {
        ext["signing.keyId"] = System.getenv("SIGNING_KEY_ID")
        ext["signing.password"] = System.getenv("SIGNING_PASSWORD")
        ext["signing.secretKeyRingFile"] = System.getenv("SIGNING_SECRET_KEY_RING_FILE")
        ext["ossrhUsername"] = System.getenv("OSSRH_USERNAME")
        ext["ossrhPassword"] = System.getenv("OSSRH_PASSWORD")
    }

    val emptyJavadocJar by tasks.registering(Jar::class) {
        archiveClassifier.set("javadoc")
    }

    afterEvaluate {
        extensions.findByType<PublishingExtension>()?.apply publishingExtension@{
            repositories {
                maven {
                    name = "sonatype"
                    setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                    credentials {
                        username = ext["ossrhUsername"]?.toString()
                        password = ext["ossrhPassword"]?.toString()
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

            extensions.findByType<SigningExtension>()?.apply {
                sign(this@publishingExtension.publications)
            }
        }
    }
}
