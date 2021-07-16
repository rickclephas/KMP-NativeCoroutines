plugins {
    kotlin("multiplatform")
    `kmp-nativecoroutines-publish`
}

kotlin {
    jvm()
    macosX64()
    iosArm64()
    iosX64()
}
