//
//  AsyncSequence.swift
//  AsyncSequence
//
//  Created by Rick Clephas on 06/03/2022.
//

import Dispatch
import Foundation
import KMPNativeCoroutinesCore

/// Converts a NativeFlow to an ``AsyncThrowingStream``
/// - Parameters:
///   - nativeFlow: the coroutine flow that is translated from kotlin to swift exposes closured. This is a typealias
///   - bufferingPolicy: The default policy in Kotlin for flows is to consume directly and suspend until another value arrived.
///   `AsyncThrowingStream` will buffer unbounded  by default if the consumer cannot directly consume the value.
///   Change it to `.bufferingNewest(1)` if you want to match the Kotlin behaviour
/// - Returns: An ``AsyncThrowingStream``  that can be consumed wit `for try await in ...`.
public func asyncSequence<Output, Failure: Error, Unit>(
  for nativeFlow: @escaping NativeFlow<Output, Failure, Unit>,
  bufferingPolicy: AsyncThrowingStream<Output, Error>.Continuation.BufferingPolicy = .unbounded
) -> AsyncThrowingStream<Output, Error> {
    AsyncThrowingStream(bufferingPolicy: bufferingPolicy) { continuation in
    let lock = NSRecursiveLock()
    
    let callbacks = FlowCallbacks<Output, Failure, Unit>(
      onItem: { item, next, unit in
        lock.lock()
        defer { lock.unlock() }
        guard !Task.isCancelled else {
          continuation.finish(throwing: CancellationError())
          return unit
        }
        continuation.yield(item)
        return next()
      },
      onComplete: { error, unit in
        lock.lock()
        defer { lock.unlock() }
        guard !Task.isCancelled else {
          continuation.finish(throwing: CancellationError())
          return unit
        }
        if let error = error {
          continuation.finish(throwing: error)
        } else {
          continuation.finish()
        }
        return unit
      },
      onCancelled: { error, unit in
        lock.lock()
        defer { lock.unlock() }
        guard !Task.isCancelled else {
          continuation.finish(throwing: CancellationError())
          return unit
        }
        continuation.finish(throwing: error)
        return unit
      }
    )
    
    let cancellable = nativeFlow(
      callbacks.onItem,
      callbacks.onComplete,
      callbacks.onCancelled
    )
    
    continuation.onTermination = { @Sendable _ in
      lock.lock()
      defer { lock.unlock() }
      _ = cancellable()
    }
  }
}

/// A struct containing the callbacks for a native Kotlin Flow.
struct FlowCallbacks<Output, Failure: Error, Unit> {
  let onItem: NativeCallback2<Output, () -> Unit, Unit>
  let onComplete: NativeCallback<Failure?, Unit>
  let onCancelled: NativeCallback<Failure, Unit>
}
