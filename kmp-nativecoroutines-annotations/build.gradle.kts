plugins {
    kotlin("multiplatform")
    `kmp-nativecoroutines-publish`
}

kotlin {
    jvm()
    macosX64()
    iosArm64()
    iosX64()
    watchosArm32()
    watchosArm64()
    watchosX64()
    tvosArm64()
    tvosX64()
}
