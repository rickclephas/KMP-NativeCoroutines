pluginManagement {
    includeBuild("..")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "sample"

include(":shared")

includeBuild("..") {
    dependencySubstitution {
        listOf("annotations", "compiler", "compiler-embeddable", "core").forEach {
            substitute(module("com.rickclephas.kmp:kmp-nativecoroutines-$it"))
                .using(project(":kmp-nativecoroutines-$it"))
        }
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
