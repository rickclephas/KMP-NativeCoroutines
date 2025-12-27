package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.name.Name

internal object Names {
    object Deprecated {
        val message = Name.identifier("message")
        val replaceWith = Name.identifier("replaceWith")
        val level = Name.identifier("level")
    }
    object Throws {
        val exceptionClasses = Name.identifier("exceptionClasses")
    }
    object ObjCName {
        val name = Name.identifier("name")
        val swiftName = Name.identifier("swiftName")
    }
}
