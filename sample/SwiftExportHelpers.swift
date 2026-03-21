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

#if NATIVE_COROUTINES_SWIFT_EXPORT
import KotlinCoroutineSupport

/// This function provides source compatibility during the migration to Swift export.
///
/// This is a no-op function and it can be safely removed once you have fully migrated to Swift export.
@available(*, deprecated, message: "Kotlin Coroutines are supported by Swift export")
public func asyncSequence<T>(
    for flow: any KotlinTypedFlow<T>
) -> KotlinFlowSequence<T> {
    return flow.asAsyncSequence()
}
#endif
