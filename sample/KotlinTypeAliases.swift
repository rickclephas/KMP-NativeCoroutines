//
//  KotlinTypeAliases.swift
//  KMPNativeCoroutinesSample
//
//  Created by Rick Clephas on 01/11/2025.
//

import NativeCoroutinesSampleShared

#if NATIVE_COROUTINES_SWIFT_EXPORT

typealias KotlinCompilerIntegrationTests = NativeCoroutinesSampleShared.tests.CompilerIntegrationTests
typealias KotlinIntergrationTests = NativeCoroutinesSampleShared.tests.IntegrationTests
typealias KotlinNewMemoryModelIntegrationTests = NativeCoroutinesSampleShared.tests.NewMemoryModelIntegrationTests
typealias KotlinSuspendIntegrationTests = NativeCoroutinesSampleShared.tests.SuspendIntegrationTests

#else

typealias KotlinCompilerIntegrationTests = NativeCoroutinesSampleShared.CompilerIntegrationTests
typealias KotlinIntergrationTests = NativeCoroutinesSampleShared.IntegrationTests
typealias KotlinNewMemoryModelIntegrationTests = NativeCoroutinesSampleShared.NewMemoryModelIntegrationTests
typealias KotlinSuspendIntegrationTests = NativeCoroutinesSampleShared.SuspendIntegrationTests

#endif
