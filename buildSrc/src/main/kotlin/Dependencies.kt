object Dependencies {

    object Kotlin {
        private const val version = "1.6.20"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val compiler = "org.jetbrains.kotlin:kotlin-compiler:$version"
        const val embeddableCompiler = "org.jetbrains.kotlin:kotlin-compiler-embeddable:$version"
    }

    object Kotlinx {
        private const val coroutinesVersion = "1.6.1-native-mt"
        const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion"
        const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion"

        const val atomicfu = "org.jetbrains.kotlinx:atomicfu:0.17.1"
    }
}
