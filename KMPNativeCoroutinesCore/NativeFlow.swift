//
//  NativeFlow.swift
//  KMPNativeCoroutinesCore
//
//  Created by Rick Clephas on 06/06/2021.
//

/// A function that collects a Kotlin coroutines Flow via callbacks.
///
/// The function takes an `onItem`, `onComplete` and `onCancelled` callback
/// and returns a cancellable that can be used to cancel the collection.
public typealias NativeFlow<Output, Failure: Error, Unit> = (
    _ onItem: @escaping NativeCallback2<Output, () -> Unit, Unit>,
    _ onComplete: @escaping NativeCallback<Failure?, Unit>,
    _ onCancelled: @escaping NativeCallback<Failure, Unit>
) -> NativeCancellable<Unit>

@available(*, deprecated, message: "Internal API used for Swift export source compatibility")
@available(iOS 13.0, macOS 10.15, tvOS 13.0, watchOS 6.0, *)
public func nativeFlow<Sequence: AsyncSequence>(for asyncSequence: Sequence) -> NativeFlow<Sequence.Element, Error, Void> {
    { onItem, onComplete, onCancelled in
        let task = Task {
            do {
                for try await element in asyncSequence {
                    await withUnsafeContinuation { continuation in
                        onItem(element, continuation.resume, ())
                    }
                }
                onComplete(nil, ())
            } catch {
                if error is CancellationError {
                    onCancelled(error, ())
                } else {
                    onComplete(error, ())
                }
            }
        }
        return { task.cancel() }
    }
}
