//
//  Observable.swift
//  KMPNativeCoroutinesRxSwift
//
//  Created by Rick Clephas on 05/07/2021.
//

import RxSwift
import KMPNativeCoroutinesCore

/// Creates an `Observable` for the provided `NativeFlow`.
/// - Parameter nativeFlow: The native flow to collect.
/// - Returns: An observable that publishes the collected values.
public func createObservable<Output, Failure: Error, Unit>(
    for nativeFlow: @escaping NativeFlow<Output, Failure, Unit>
) -> Observable<Output> {
    return createObservableImpl(for: nativeFlow)
}

/// This function provides source compatibility during the migration to Swift export.
///
/// You should migrate away from this function once you have fully migrated to Swift export.
@available(*, deprecated, message: "Kotlin Coroutines are supported by Swift export")
@available(iOS 13.0, macOS 10.15, tvOS 13.0, watchOS 6.0, *)
public func createObservable<Sequence: AsyncSequence>(
    for asyncSequence: Sequence
) -> Observable<Sequence.Element> {
    return createObservableImpl(for: nativeFlow(for: asyncSequence))
}

/// Creates an `Observable` for the provided `NativeFlow`.
/// - Parameter nativeFlow: The native flow to collect.
/// - Returns: An observable that publishes the collected values.
public func createObservable<Unit, Failure: Error>(
    for nativeFlow: @escaping NativeFlow<Unit, Failure, Unit>
) -> Observable<Void> {
    return createObservableImpl(for: nativeFlow).map { _ in }
}

private func createObservableImpl<Output, Failure: Error, Unit>(
    for nativeFlow: @escaping NativeFlow<Output, Failure, Unit>
) -> Observable<Output> {
    return Observable.deferred {
        return Observable.create { observer in
            let nativeCancellable = nativeFlow({ item, next, _ in
                observer.onNext(item)
                return next()
            }, { error, unit in
                if let error = error {
                    observer.onError(error)
                } else {
                    observer.onCompleted()
                }
                return unit
            }, { cancellationError, unit in
                observer.onError(cancellationError)
                return unit
            })
            return Disposables.create { _ = nativeCancellable() }
        }
    }
}
