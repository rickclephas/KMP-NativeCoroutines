plugins {
    kotlin("multiplatform")
}

group = "com.rickclephas.kmp"
version = "0.1.0-SNAPSHOT"

kotlin {
    macosX64()
    iosArm64()
    iosX64()
    sourceSets {
        val commonMain by getting
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
