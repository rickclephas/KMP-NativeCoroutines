package com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics

import com.intellij.lang.LighterASTNode
import com.intellij.openapi.util.Ref
import com.intellij.openapi.util.TextRange
import com.intellij.util.diff.FlyweightCapableTreeStructure
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.diagnostics.*
import org.jetbrains.kotlin.lexer.KtTokens

internal object CustomLightTreePositioningStrategies {

    // Same as in LightTreePositioningStrategies
    private val LighterASTNode.isDeclaration: Boolean
        get() =
            when (tokenType) {
                KtNodeTypes.PRIMARY_CONSTRUCTOR, KtNodeTypes.SECONDARY_CONSTRUCTOR,
                KtNodeTypes.FUN, KtNodeTypes.FUNCTION_LITERAL,
                KtNodeTypes.PROPERTY,
                KtNodeTypes.PROPERTY_ACCESSOR,
                KtNodeTypes.CLASS,
                KtNodeTypes.OBJECT_DECLARATION,
                KtNodeTypes.CLASS_INITIALIZER ->
                    true
                else ->
                    false
            }

    // Same as in LightTreePositioningStrategies
    private fun FlyweightCapableTreeStructure<LighterASTNode>.typeReference(node: LighterASTNode): LighterASTNode? {
        val childrenRef = Ref<Array<LighterASTNode?>>()
        getChildren(node, childrenRef)
        return childrenRef.get()?.filterNotNull()?.dropWhile { it.tokenType != KtTokens.COLON }?.firstOrNull {
            it.tokenType == KtNodeTypes.TYPE_REFERENCE
        }
    }

    val DECLARATION_RETURN_TYPE = object : LightTreePositioningStrategy() {
        override fun mark(
            node: LighterASTNode,
            startOffset: Int,
            endOffset: Int,
            tree: FlyweightCapableTreeStructure<LighterASTNode>
        ): List<TextRange> {
            if (node.isDeclaration) {
                return LightTreePositioningStrategies.DECLARATION_RETURN_TYPE.mark(node, startOffset, endOffset, tree)
            }
            return markElement(getElementToMark(node, tree), startOffset, endOffset, tree, node)
        }

        override fun isValid(node: LighterASTNode, tree: FlyweightCapableTreeStructure<LighterASTNode>): Boolean {
            if (node.isDeclaration) {
                return LightTreePositioningStrategies.DECLARATION_RETURN_TYPE.isValid(node, tree)
            }
            return super.isValid(getElementToMark(node, tree), tree)
        }

        private fun getElementToMark(node: LighterASTNode, tree: FlyweightCapableTreeStructure<LighterASTNode>): LighterASTNode =
            if (node.tokenType == KtNodeTypes.VALUE_PARAMETER) tree.typeReference(node) ?: node else node
    }
}
