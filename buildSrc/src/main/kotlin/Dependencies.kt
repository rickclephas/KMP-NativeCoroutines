object Dependencies {

    object Kotlin {
        private const val version = "1.5.30"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val compiler = "org.jetbrains.kotlin:kotlin-compiler:$version"
    }

    object Kotlinx {
        const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2-native-mt"
    }
}