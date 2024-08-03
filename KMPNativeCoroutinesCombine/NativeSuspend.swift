//
//  NativeSuspend.swift
//  KMPNativeCoroutinesCombine
//
//  Created by Rick Clephas on 04/09/2023.
//

import Combine
import Dispatch
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
    
    private let semaphore = DispatchSemaphore(value: 1)
    
    private let onResult: NativeCallback1<Output>
    private let onError: NativeCallback1<Error>
    private let onCancelled: NativeCallback1<Error>
    
    init(
        _ onResult: @escaping NativeCallback1<Output>,
        _ onError: @escaping NativeCallback1<Error>,
        _ onCancelled: @escaping NativeCallback1<Error>
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
        semaphore.wait()
        guard subscription != nil else {
            semaphore.signal()
            return
        }
        self.subscription = nil
        semaphore.signal()
        switch completion {
        case .finished:
            guard let result else {
                fatalError("A NativeSuspend must receive a single value before completing")
            }
            _ = onResult(result)
            break
        case let .failure(error):
            _ = onError(error)
            break
        }
    }
    
    func cancel() {
        semaphore.wait()
        guard let subscription else {
            semaphore.signal()
            return
        }
        self.subscription = nil
        semaphore.signal()
        subscription.cancel()
        _ = onCancelled(CancellationError())
    }
}
