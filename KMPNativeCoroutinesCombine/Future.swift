//
//  Future.swift
//  KMPNativeCoroutinesCombine
//
//  Created by Rick Clephas on 06/06/2021.
//

import Combine
import KMPNativeCoroutinesCore

internal let RETURN_TYPE_COMBINE_FUTURE = "combine-future"

/// Creates an `AnyPublisher` for the provided `NativeSuspend`.
/// - Parameter nativeSuspend: The native suspend function to await.
/// - Returns: A publisher that either finishes with a single value or fails with an error.
public func createFuture<Result, Failure: Error>(
    for nativeSuspend: @escaping NativeSuspend<Result, Failure>
) -> AnyPublisher<Result, Failure> {
    if let publisher = nativeSuspend(RETURN_TYPE_COMBINE_FUTURE, EmptyNativeCallback, EmptyNativeCallback, EmptyNativeCallback)() {
        return publisher as! AnyPublisher<Result, Failure>
    }
    return NativeSuspendFuture(nativeSuspend: nativeSuspend).eraseToAnyPublisher()
}

internal struct NativeSuspendFuture<Result, Failure: Error>: Publisher {
    
    typealias Output = Result
    typealias Failure = Failure
    
    let nativeSuspend: NativeSuspend<Result, Failure>
    
    func receive<S>(subscriber: S) where S : Subscriber, Failure == S.Failure, Result == S.Input {
        let subscription = NativeSuspendSubscription(nativeSuspend: nativeSuspend, subscriber: subscriber)
        subscriber.receive(subscription: subscription)
    }
}

internal class NativeSuspendSubscription<Result, Failure, S: Subscriber>: Subscription where S.Input == Result, S.Failure == Failure {
    
    private var nativeSuspend: NativeSuspend<Result, Failure>?
    private var nativeCancellable: NativeCancellable? = nil
    private var subscriber: S?
    
    init(nativeSuspend: @escaping NativeSuspend<Result, Failure>, subscriber: S) {
        self.nativeSuspend = nativeSuspend
        self.subscriber = subscriber
    }
    
    func request(_ demand: Subscribers.Demand) {
        guard let nativeSuspend = nativeSuspend, demand >= 1 else { return }
        self.nativeSuspend = nil
        nativeCancellable = nativeSuspend(nil, { output, unit in
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
