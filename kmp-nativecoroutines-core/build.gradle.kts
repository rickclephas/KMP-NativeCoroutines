plugins {
    kotlin("multiplatform")
    `maven-publish`
    signing
}

kotlin {
    jvm()
    macosX64()
    iosArm64()
    iosX64()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(Dependencies.Kotlinx.coroutinesCore)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val appleMain by creating {
            dependsOn(commonMain)
        }
        val appleTest by creating {
            dependsOn(commonTest)
        }
        val macosX64Main by getting {
            dependsOn(appleMain)
        }
        val macosX64Test by getting {
            dependsOn(appleTest)
        }
        val iosArm64Main by getting {
            dependsOn(appleMain)
        }
        val iosArm64Test by getting {
            dependsOn(appleTest)
        }
        val iosX64Main by getting {
            dependsOn(appleMain)
        }
        val iosX64Test by getting {
            dependsOn(appleTest)
        }
    }
}
