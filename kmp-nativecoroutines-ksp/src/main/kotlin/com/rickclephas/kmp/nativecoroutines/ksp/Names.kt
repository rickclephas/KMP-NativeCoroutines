package com.rickclephas.kmp.nativecoroutines.ksp

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName

private const val packageName = "com.rickclephas.kmp.nativecoroutines"

internal const val nativeCoroutinesAnnotationName = "$packageName.NativeCoroutines"
internal const val nativeCoroutinesStateAnnotationName = "$packageName.NativeCoroutinesState"
internal const val nativeCoroutineScopeAnnotationName = "$packageName.NativeCoroutineScope"

internal val nativeSuspendMemberName = MemberName(packageName, "nativeSuspend")
internal val nativeSuspendClassName = ClassName(packageName, "NativeSuspend")

internal val asNativeFlowMemberName = MemberName(packageName, "asNativeFlow")
internal val nativeFlowClassName = ClassName(packageName, "NativeFlow")

internal val runMemberName = MemberName("kotlin", "run")
internal val objCNameAnnotationClassName = ClassName("kotlin.native", "ObjCName")
internal const val throwsAnnotationName = "kotlin.Throws"
