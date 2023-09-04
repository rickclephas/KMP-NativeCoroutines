//
//  NativeFlow.swift
//  KMPNativeCoroutinesCombine
//
//  Created by Rick Clephas on 04/09/2023.
//

import Combine
import KMPNativeCoroutinesCore

public extension Publisher {
    /// Creates a `NativeFlow` for this `Publisher`.
    func asNativeFlow() -> NativeFlow<Output, Error> {
        return { returnType, onItem, onComplete, onCancelled in
            if returnType == RETURN_TYPE_COMBINE_PUBLISHER {
                return { self.eraseToAnyPublisher() }
            } else if returnType != nil {
                return { nil }
            }
            let subscriber = NativeFlowSubsriber<Output, Failure>(onItem, onComplete, onCancelled)
            subscribe(subscriber)
            return subscriber.asNativeCancellable()
        }
    }
}

internal class NativeFlowSubsriber<Output, Failure: Error>: Subscriber, Cancellable {
    
    typealias Input = Output
    typealias Failure = Failure
    
    private let onItem: NativeCallback2<Output, () -> NativeUnit>
    private let onComplete: NativeCallback<Error?>
    private let onCancelled: NativeCallback<Error>
    
    init(
        _ onItem: @escaping NativeCallback2<Output, () -> NativeUnit>,
        _ onComplete: @escaping NativeCallback<Error?>,
        _ onCancelled: @escaping NativeCallback<Error>
    ) {
        self.onItem = onItem
        self.onComplete = onComplete
        self.onCancelled = onCancelled
    }
    
    private var subscription: Subscription? = nil
    
    func receive(subscription: Subscription) {
        self.subscription = subscription
        subscription.request(.max(1))
    }
    
    func receive(_ input: Output) -> Subscribers.Demand {
        _ = onItem(input, {
            self.subscription?.request(.max(1))
            return ()
        }, ())
        return .none
    }
    
    func receive(completion: Subscribers.Completion<Failure>) {
        switch completion {
        case .finished:
            _ = onComplete(nil, ())
            break;
        case let .failure(error):
            _ = onComplete(error, ())
            break;
        }
    }
    
    func cancel() {
        subscription?.cancel()
        subscription = nil
        _ = onCancelled(CancellationError(), ())
    }
}
