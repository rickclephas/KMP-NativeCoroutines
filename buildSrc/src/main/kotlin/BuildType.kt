import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.registering

enum class BuildType {
    COMPILER_TESTS, IDE_PLUGIN
}

val Project.buildType: Provider<BuildType>
    get() = providers.gradleProperty("buildType")
        .filter { it.isNotEmpty() }
        .map(BuildType::valueOf)

fun Project.requireBuildType(buildType: BuildType) = tasks.registering {
    val actualBuildType = this@requireBuildType.buildType
    doFirst {
        if (actualBuildType.orNull != buildType) {
            throw IllegalStateException("Build type ${buildType.name} is required. Please run with -PbuildType=${buildType.name}")
        }
    }
}
