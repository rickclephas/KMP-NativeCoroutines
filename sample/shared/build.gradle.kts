import org.jetbrains.kotlin.gradle.swiftexport.ExperimentalSwiftExportDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.plugin.serialization)
    id("com.rickclephas.kmp.nativecoroutines")
}

version = "1.0"

kotlin {
    jvmToolchain(11)

    explicitApi()
    val macosArm64 = macosArm64()
    val iosArm64 = iosArm64()
    val iosX64 = iosX64()
    val iosSimulatorArm64 = iosSimulatorArm64()
    val watchosArm64 = watchosArm64()
    val watchosSimulatorArm64 = watchosSimulatorArm64()
    val tvosArm64 = tvosArm64()
    val tvosSimulatorArm64 = tvosSimulatorArm64()
    sourceSets {
        all {
            languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
        }
        commonMain {
            dependencies {
                implementation(libs.kotlinx.serialization.json)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        listOf(
            macosArm64,
            iosArm64, iosX64, iosSimulatorArm64,
            watchosArm64, watchosSimulatorArm64,
            tvosArm64, tvosSimulatorArm64
        ).forEach {
            it.binaries.framework {
                baseName = "NativeCoroutinesSampleShared"
            }
        }
    }
    export {
        @OptIn(ExperimentalSwiftExportDsl::class)
        swift {
            moduleName = "NativeCoroutinesSampleShared"
            rootPackage = "com.rickclephas.kmp.nativecoroutines.sample"
            xcodeIntegration()
        }
    }
}

nativeCoroutines {
    swiftExport = System.getenv("NATIVE_COROUTINES_SWIFT_EXPORT")?.toBooleanStrictOrNull() == true
}
