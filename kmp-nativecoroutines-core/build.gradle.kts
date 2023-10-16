plugins {
    alias(libs.plugins.kotlin.multiplatform)
    `kmp-nativecoroutines-publish`
}

kotlin {
    explicitApi()
    jvmToolchain(11)
    applyDefaultHierarchyTemplate()

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
    jvm()
    js {
        browser()
        nodejs()
    }
    linuxArm64()
    linuxX64()
    mingwX64()

    targets.all {
        compilations.all {
            compilerOptions.configure {
                freeCompilerArgs.add("-Xexpect-actual-classes")
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
        val nativeCoroutinesMain by creating {
            dependsOn(commonMain.get())
        }
        val nativeCoroutinesTest by creating {
            dependsOn(commonTest.get())
        }
        appleMain {
            dependsOn(nativeCoroutinesMain)
        }
        appleTest {
            dependsOn(nativeCoroutinesTest)
        }
    }
}
