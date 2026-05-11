//
//  SwiftExportHelpers.swift
//  KMPNativeCoroutinesSample
//
//  Created by Rick Clephas on 01/11/2025.
//

import NativeCoroutinesSampleShared

#if NATIVE_COROUTINES_SWIFT_EXPORT

typealias KotlinCompilerIntegrationTests = NativeCoroutinesSampleShared.tests.CompilerIntegrationTests
typealias KotlinFlowIntegrationTests = NativeCoroutinesSampleShared.tests.FlowIntegrationTests
typealias KotlinIntegrationTests = NativeCoroutinesSampleShared.tests.IntegrationTests
typealias KotlinNewMemoryModelIntegrationTests = NativeCoroutinesSampleShared.tests.NewMemoryModelIntegrationTests
typealias KotlinSuspendIntegrationTests = NativeCoroutinesSampleShared.tests.SuspendIntegrationTests
typealias KotlinThreadLockIntegrationTests = NativeCoroutinesSampleShared.tests.ThreadLockIntegrationTests

#else

typealias KotlinCompilerIntegrationTests = NativeCoroutinesSampleShared.CompilerIntegrationTests
typealias KotlinFlowIntegrationTests = NativeCoroutinesSampleShared.FlowIntegrationTests
typealias KotlinIntegrationTests = NativeCoroutinesSampleShared.IntegrationTests
typealias KotlinNewMemoryModelIntegrationTests = NativeCoroutinesSampleShared.NewMemoryModelIntegrationTests
typealias KotlinSuspendIntegrationTests = NativeCoroutinesSampleShared.SuspendIntegrationTests
typealias KotlinThreadLockIntegrationTests = NativeCoroutinesSampleShared.ThreadLockIntegrationTests

#endif

func setup<T: KotlinIntegrationTests>(_ init: () -> T) -> T {
    let integrationTests = `init`()
    #if NATIVE_COROUTINES_SWIFT_EXPORT
    integrationTests.isTestingSwiftExport = true
    #endif
    return integrationTests
}
