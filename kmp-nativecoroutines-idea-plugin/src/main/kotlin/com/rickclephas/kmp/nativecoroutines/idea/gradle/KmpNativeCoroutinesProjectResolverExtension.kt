package com.rickclephas.kmp.nativecoroutines.idea.gradle

import com.intellij.openapi.externalSystem.model.DataNode
import com.intellij.openapi.externalSystem.model.project.ModuleData
import org.gradle.tooling.model.idea.IdeaModule
import org.jetbrains.plugins.gradle.service.project.AbstractProjectResolverExtension

class KmpNativeCoroutinesProjectResolverExtension: AbstractProjectResolverExtension() {

    override fun getExtraProjectModelClasses() = setOf(KmpNativeCoroutinesModel::class.java)

    override fun getToolingExtensionsClasses() = setOf(KmpNativeCoroutinesModelBuilderService::class.java, Unit::class.java)

    override fun populateModuleExtraModels(gradleModule: IdeaModule, ideModule: DataNode<ModuleData>) {
        resolverCtx.getExtraProject(gradleModule, KmpNativeCoroutinesModel::class.java)?.let {
            ideModule.putCopyableUserData(KmpNativeCoroutinesModelImpl.KEY, it)
        }
        super.populateModuleExtraModels(gradleModule, ideModule)
    }
}
