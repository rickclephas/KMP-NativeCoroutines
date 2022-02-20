plugins {
    kotlin("multiplatform")
    `kmp-nativecoroutines-publish`
}

kotlin {
    val macosX64 = macosX64()
    val macosArm64 = macosArm64()
    val iosArm64 = iosArm64()
    val iosX64 = iosX64()
    val iosSimulatorArm64 = iosSimulatorArm64()
    val watchosArm32 = watchosArm32()
    val watchosArm64 = watchosArm64()
    val watchosX64 = watchosX64()
    val watchosSimulatorArm64 = watchosSimulatorArm64()
    val tvosArm64 = tvosArm64()
    val tvosX64 = tvosX64()
    val tvosSimulatorArm64 = tvosSimulatorArm64()
    jvm()
    js(BOTH) {
        browser()
        nodejs()
    }
    linuxX64()
    mingwX64()
    sourceSets {
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlin.native.internal.InternalForKotlinNative")
        }
        val commonMain by getting {
            dependencies {
                api(Dependencies.Kotlinx.coroutinesCore)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(Dependencies.Kotlinx.atomicfu)
            }
        }
        val nativeCoroutinesMain by creating {
            dependsOn(commonMain)
        }
        val nativeCoroutinesTest by creating {
            dependsOn(commonTest)
        }
        val appleMain by creating {
            dependsOn(nativeCoroutinesMain)
            dependencies {
                api("com.rickclephas.kmp:nserror-kt:0.1.0-SNAPSHOT")
            }
        }
        val appleTest by creating {
            dependsOn(nativeCoroutinesTest)
        }
        listOf(
            macosX64, macosArm64,
            iosArm64, iosX64, iosSimulatorArm64,
            watchosArm32, watchosArm64, watchosX64, watchosSimulatorArm64,
            tvosArm64, tvosX64, tvosSimulatorArm64
        ).forEach {
            getByName("${it.targetName}Main") {
                dependsOn(appleMain)
            }
            getByName("${it.targetName}Test") {
                dependsOn(appleTest)
            }
        }
    }
}
