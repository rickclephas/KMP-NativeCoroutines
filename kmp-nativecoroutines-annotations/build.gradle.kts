plugins {
    alias(libs.plugins.kotlin.multiplatform)
    `kmp-nativecoroutines-publish`
}

kotlin {
    explicitApi()
    jvmToolchain(11)

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
}
