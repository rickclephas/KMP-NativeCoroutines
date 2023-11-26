//
//  NativeSuspend.swift
//  KMPNativeCoroutinesCombine
//
//  Created by Rick Clephas on 04/09/2023.
//

import Combine
import KMPNativeCoroutinesCore

public extension Publisher {
    /// Creates a `NativeSuspend` for this `Publisher`.
    func asNativeSuspend() -> NativeSuspend<Output, Error> {
        return { returnType, onResult, onError, onCancelled in
            if returnType == RETURN_TYPE_COMBINE_FUTURE {
                return { self.mapError({ error -> Error in error }).eraseToAnyPublisher() }
            } else if returnType != nil {
                return { nil }
            }
            let subscriber = NativeSuspendSubsriber<Output, Failure>(onResult, onError, onCancelled)
            subscribe(subscriber)
            return subscriber.asNativeCancellable()
        }
    }
}

internal class NativeSuspendSubsriber<Output, Failure: Error>: Subscriber, Cancellable {
    
    typealias Input = Output
    typealias Failure = Failure
    
    private let onResult: NativeCallback<Output>
    private let onError: NativeCallback<Error>
    private let onCancelled: NativeCallback<Error>
    
    init(
        _ onResult: @escaping NativeCallback<Output>,
        _ onError: @escaping NativeCallback<Error>,
        _ onCancelled: @escaping NativeCallback<Error>
    ) {
        self.onResult = onResult
        self.onError = onError
        self.onCancelled = onCancelled
    }
    
    private var subscription: Subscription? = nil
    
    func receive(subscription: Subscription) {
        self.subscription = subscription
        subscription.request(.max(1))
    }
    
    private var result: Output? = nil
    
    func receive(_ input: Output) -> Subscribers.Demand {
        guard result == nil else {
            fatalError("A NativeSuspend can't receive more than a single value")
        }
        result = input
        return .none
    }
    
    func receive(completion: Subscribers.Completion<Failure>) {
        guard subscription != nil else { return }
        switch completion {
        case .finished:
            guard let result else {
                fatalError("A NativeSuspend must receive a single value before completing")
            }
            _ = onResult(result, ())
            break
        case let .failure(error):
            _ = onError(error, ())
            break
        }
    }
    
    func cancel() {
        guard let subscription else { return }
        self.subscription = nil
        subscription.cancel()
        _ = onCancelled(CancellationError(), ())
    }
}
