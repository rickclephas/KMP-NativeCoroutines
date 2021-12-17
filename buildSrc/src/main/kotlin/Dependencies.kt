object Dependencies {

    object Kotlin {
        private const val version = "1.6.10"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val compiler = "org.jetbrains.kotlin:kotlin-compiler:$version"
        const val embeddableCompiler = "org.jetbrains.kotlin:kotlin-compiler-embeddable:$version"
    }

    object Kotlinx {
        const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-RC3"
    }
}