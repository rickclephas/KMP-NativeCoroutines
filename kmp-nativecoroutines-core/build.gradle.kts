import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    `kmp-nativecoroutines-publish`
}

val buildForCompilerTest = (findProperty("buildForCompilerTest") as String?)
    ?.toBooleanStrictOrNull() ?: false

kotlin {
    explicitApi()
    jvmToolchain(11)

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    applyDefaultHierarchyTemplate {
        common {
            group("nativeCoroutines") {
                group("apple")
                if (buildForCompilerTest) {
                    withJvm()
                }
            }
        }
    }

    macosX64()
    macosArm64()
    iosArm64()
    iosX64()
    iosSimulatorArm64()
    watchosArm32()
    watchosArm64()
    watchosX64()
    watchosSimulatorArm64()
    watchosDeviceArm64()
    tvosArm64()
    tvosX64()
    tvosSimulatorArm64()
    jvm {
        if (buildForCompilerTest) {
            val mainCompilation = compilations.getByName(KotlinCompilation.MAIN_COMPILATION_NAME)
            mainCompilation.defaultSourceSet.kotlin.srcDir("src/compilerTestMain/kotlin")
            val testCompilation = compilations.getByName(KotlinCompilation.TEST_COMPILATION_NAME)
            testCompilation.defaultSourceSet.kotlin.srcDir("src/compilerTestTest/kotlin")
        }
    }
    js {
        browser()
        nodejs()
    }
    linuxArm64()
    linuxX64()
    mingwX64()
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
        d8()
    }

    targets.all {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.add("-Xexpect-actual-classes")
                }
            }
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                api(libs.kotlinx.coroutines.core)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}
