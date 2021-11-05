package com.rickclephas.kmp.nativecoroutines

/**
 * The core library only contains code for Apple targets.
 * There is an issue with publication for other targets without source code though.
 * So this private constant will make sure all targets have source code.
 */
@Suppress("unused")
private const val EMPTY = 1