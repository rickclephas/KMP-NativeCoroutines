plugins {
    kotlin("multiplatform")
    `kmp-nativecoroutines-publish`
}

kotlin {
    macosX64()
    iosArm64()
    iosX64()
    watchosArm32()
    watchosArm64()
    watchosX64()
    tvosArm64()
    tvosX64()
    jvm()
    js(BOTH) {
        browser()
        nodejs()
    }
    linuxX64()
}
