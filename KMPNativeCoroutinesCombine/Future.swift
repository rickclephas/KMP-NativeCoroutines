//
//  Future.swift
//  KMPNativeCoroutinesCombine
//
//  Created by Rick Clephas on 06/06/2021.
//

import Combine
import KMPNativeCoroutinesCore

/// Creates an `AnyPublisher` for the provided `NativeSuspend`.
/// - Parameter nativeSuspend: The native suspend function to await.
/// - Returns: A publisher that either finishes with a single value or fails with an error.
@available(*, deprecated)
public func createFuture<Result, Failure: Error, Unit>(
    for nativeSuspend: @escaping NativeSuspend<Result, Failure, Unit>
) -> AnyPublisher<Result, Failure> {
    return NativeSuspendFuture(nativeSuspend: nativeSuspend)
        .eraseToAnyPublisher()
}

public func createFuture<Result, Failure: Error, Unit>(
    for nativeSuspend: @escaping () -> NativeSuspend<Result, Failure, Unit>
) -> AnyPublisher<Result, Failure> {
    return NativeSuspendFuture(nativeSuspend: nativeSuspend())
        .eraseToAnyPublisher()
}

@available(*, deprecated)
public func createFuture<Result>(for result: @escaping () async throws -> Result) -> AnyPublisher<Result, Error> {
    Future<Result, Error> { promise in
        Task {
            do {
                promise(.success(try await result()))
            } catch {
                promise(.failure(error))
            }
        }
        // TODO: Support cancellation
    }.eraseToAnyPublisher()
}

/// Creates an `AnyPublisher` for the provided `NativeSuspend`.
/// - Parameter nativeSuspend: The native suspend function to await.
/// - Returns: A publisher that either finishes with a single value or fails with an error.
public func createFuture<Unit, Failure: Error>(
    for nativeSuspend: @escaping NativeSuspend<Unit, Failure, Unit>
) -> AnyPublisher<Void, Failure> {
    return NativeSuspendFuture(nativeSuspend: nativeSuspend)
        .map { _ in }
        .eraseToAnyPublisher()
}

internal struct NativeSuspendFuture<Result, Failure: Error, Unit>: Publisher {
    
    typealias Output = Result
    typealias Failure = Failure
    
    let nativeSuspend: NativeSuspend<Result, Failure, Unit>
    
    func receive<S>(subscriber: S) where S : Subscriber, Failure == S.Failure, Result == S.Input {
        let subscription = NativeSuspendSubscription(nativeSuspend: nativeSuspend, subscriber: subscriber)
        subscriber.receive(subscription: subscription)
    }
}

internal class NativeSuspendSubscription<Result, Failure, Unit, S: Subscriber>: Subscription where S.Input == Result, S.Failure == Failure {
    
    private var nativeSuspend: NativeSuspend<Result, Failure, Unit>?
    private var nativeCancellable: NativeCancellable<Unit>? = nil
    private var subscriber: S?
    
    init(nativeSuspend: @escaping NativeSuspend<Result, Failure, Unit>, subscriber: S) {
        self.nativeSuspend = nativeSuspend
        self.subscriber = subscriber
    }
    
    func request(_ demand: Subscribers.Demand) {
        guard let nativeSuspend = nativeSuspend, demand >= 1 else { return }
        self.nativeSuspend = nil
        nativeCancellable = nativeSuspend({ output, unit in
            if let subscriber = self.subscriber {
                _ = subscriber.receive(output)
                subscriber.receive(completion: .finished)
            }
            return unit
        }, { error, unit in
            self.subscriber?.receive(completion: .failure(error))
            return unit
        }, { cancellationError, unit in
            self.subscriber?.receive(completion: .failure(cancellationError))
            return unit
        })
    }
    
    func cancel() {
        subscriber = nil
        nativeSuspend = nil
        _ = nativeCancellable?()
        nativeCancellable = nil
    }
}
