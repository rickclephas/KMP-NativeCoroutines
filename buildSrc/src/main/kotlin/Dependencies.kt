object Dependencies {

    object Kotlin {
        private const val version = "1.5.20"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val compiler = "org.jetbrains.kotlin:kotlin-compiler:$version"
    }

    object Kotlinx {
        const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0-native-mt"
    }

    object AutoService {
        private const val version = "1.0"
        const val annotations = "com.google.auto.service:auto-service-annotations:$version"
        const val processor = "com.google.auto.service:auto-service:$version"
    }
}
