package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName

internal val hidesFromObjCFqName = FqName("kotlin.native.HidesFromObjC")
internal val hidesFromObjCClassId = ClassId.topLevel(hidesFromObjCFqName)
internal val refinesInSwiftFqName = FqName("kotlin.native.RefinesInSwift")
internal val refinesInSwiftClassId = ClassId.topLevel(refinesInSwiftFqName)
