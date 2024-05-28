plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.ksp)
    id("com.rickclephas.kmp.nativecoroutines")
}

version = "1.0"

kotlin {
    jvmToolchain(11)

    explicitApi()
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
    sourceSets {
        all {
            languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
        }
        commonMain {
            dependencies {
                implementation(libs.kotlinx.serialization.json)
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        listOf(
            macosX64, macosArm64,
            iosArm64, iosX64, iosSimulatorArm64,
            watchosArm32, watchosArm64, watchosX64, watchosSimulatorArm64,
            tvosArm64, tvosX64, tvosSimulatorArm64
        ).forEach {
            it.binaries.framework {
                baseName = "NativeCoroutinesSampleShared"
            }
        }
    }
}
