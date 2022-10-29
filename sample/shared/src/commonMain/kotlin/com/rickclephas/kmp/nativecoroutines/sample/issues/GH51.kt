package com.rickclephas.kmp.nativecoroutines.sample.issues

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.*

interface InterfaceA {
    @NativeCoroutines
    fun foo(value: Int): Flow<Int>
    @NativeCoroutines
    val bar: Flow<Int>
}

interface InterfaceB {
    @NativeCoroutines
    fun foo(value: Int): Flow<Int>
    @NativeCoroutines
    val bar: Flow<Int>
}

interface InterfaceC: InterfaceA, InterfaceB {
    // We need to override these else the compiler plugin won't generate `fooNative` and `barNative`.
    // Those must be overridden since both `InterfaceA` and `InterfaceB` have their own implementations.
    // Note: all implementations are actually identical, but that isn't known by the compiler.
//    @NativeCoroutines
    override fun foo(value: Int): Flow<Int>
//    @NativeCoroutines
    override val bar: Flow<Int>
}

class InterfaceCImpl: InterfaceC {
    override fun foo(value: Int): Flow<Int> = flow { emit(value) }
    override val bar: Flow<Int> = flow { emit(1) }
}

abstract class ClassC: InterfaceA, InterfaceB {
    // We need to override these else the compiler plugin won't generate `fooNative` and `barNative`.
    // Those must be overridden since both `InterfaceA` and `InterfaceB` have their own implementations.
    // Note: all implementations are actually identical, but that isn't known by the compiler.
//    @NativeCoroutines
    abstract override fun foo(value: Int): Flow<Int>
//    @NativeCoroutines
    abstract override val bar: Flow<Int>
}

class ClassCImpl: ClassC() {
    override fun foo(value: Int): Flow<Int> = flow { emit(value) }
    override val bar: Flow<Int> = flow { emit(1) }
}
