plugins {
    kotlin("multiplatform")
    `kmp-nativecoroutines-publish`
}

kotlin {
    jvm()
    val macosX64 = macosX64()
    val iosArm64 = iosArm64()
    val iosX64 = iosX64()
    val watchosArm32 = watchosArm32()
    val watchosArm64 = watchosArm64()
    val watchosX64 = watchosX64()
    val tvosArm64 = tvosArm64()
    val tvosX64 = tvosX64()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(Dependencies.Kotlinx.coroutinesCore)
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
}
