object Dependencies {

    object Kotlin {
        private const val version = "1.6.20-M1"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val compiler = "org.jetbrains.kotlin:kotlin-compiler:$version"
        const val embeddableCompiler = "org.jetbrains.kotlin:kotlin-compiler-embeddable:$version"
    }

    object Kotlinx {
        const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-native-mt"

        const val atomicfu = "org.jetbrains.kotlinx:atomicfu:0.17.1"
    }
}
