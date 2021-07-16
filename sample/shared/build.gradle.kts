plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.rickclephas.kmp.nativecoroutines") version "0.3.0"
}

kotlin {
    macosX64()
    iosArm64()
    iosX64()
    sourceSets {
        all {
            languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
        }
        val commonMain by getting {
            dependencies {
                implementation(Dependencies.Kotlinx.datetime)
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
    cocoapods {
        summary = "Shared Kotlin code for the KMP-NativeCoroutines Sample"
        homepage = "https://github.com/rickclephas/KMP-NativeCoroutines"
        frameworkName = "NativeCoroutinesSampleShared"
        ios.deploymentTarget = "13.0"
        osx.deploymentTarget = "10.15"
        podfile = project.file("../Podfile")
    }
}