plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.rickclephas.kmp.nativecoroutines")
}

version = "1.0"

kotlin {
    val macosX64 = macosX64()
    val iosArm64 = iosArm64()
    val iosX64 = iosX64()
    val watchosArm32 = watchosArm32()
    val watchosArm64 = watchosArm64()
    val watchosX64 = watchosX64()
    val tvosArm64 = tvosArm64()
    val tvosX64 = tvosX64()
    sourceSets {
        all {
            languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
        }
        val commonMain by getting {
            dependencies {

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
        listOf(
            macosX64,
            iosArm64, iosX64,
            watchosArm32, watchosArm64, watchosX64,
            tvosArm64, tvosX64
        ).forEach {
            getByName("${it.targetName}Main") {
                dependsOn(appleMain)
            }
            getByName("${it.targetName}Test") {
                dependsOn(appleTest)
            }
        }
    }
    cocoapods {
        summary = "Shared Kotlin code for the KMP-NativeCoroutines Sample"
        homepage = "https://github.com/rickclephas/KMP-NativeCoroutines"
        frameworkName = "NativeCoroutinesSampleShared"
        ios.deploymentTarget = "13.0"
        osx.deploymentTarget = "10.15"
        watchos.deploymentTarget = "6.0"
        tvos.deploymentTarget = "13.0"
        podfile = project.file("../Podfile")
    }
}

afterEvaluate {
    tasks.withType(org.jetbrains.kotlin.gradle.tasks.FatFrameworkTask::class.java).forEach {
        it.baseName = "NativeCoroutinesSampleShared"
    }
}
