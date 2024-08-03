//
//  NativeFlow.swift
//  KMPNativeCoroutinesCombine
//
//  Created by Rick Clephas on 04/09/2023.
//

import Combine
import Dispatch
import KMPNativeCoroutinesCore

public extension Publisher {
    /// Creates a `NativeFlow` for this `Publisher`.
    func asNativeFlow() -> NativeFlow<Output, Error> {
        return { returnType, onItem, onComplete, onCancelled in
            if returnType == RETURN_TYPE_COMBINE_PUBLISHER {
                return { self.mapError({ error -> Error in error }).eraseToAnyPublisher() }
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
    
    enum State {
        case onItem
        case onComplete(completion: Subscribers.Completion<Failure>)
        case onCancelled
    }
    
    private var state: State? = nil
    private let semaphore = DispatchSemaphore(value: 1)
    
    private let onItem: NativeCallback2<Output, NativeCallback>
    private let onComplete: NativeCallback1<Error?>
    private let onCancelled: NativeCallback1<Error>
    
    init(
        _ onItem: @escaping NativeCallback2<Output, NativeCallback>,
        _ onComplete: @escaping NativeCallback1<Error?>,
        _ onCancelled: @escaping NativeCallback1<Error>
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
        semaphore.wait()
        switch state {
        case .onItem:
            fatalError("NativeFlow can only process a single value at a time")
        case .onComplete, .onCancelled:
            semaphore.signal()
            return .none
        case nil:
            state = .onItem
        }
        semaphore.signal()
        _ = onItem(input, {
            self.semaphore.wait()
            switch self.state {
            case nil:
                fatalError("NativeFlow isn't in the expected onItem state")
            case .onItem:
                self.state = nil
                self.semaphore.signal()
            case .onComplete(let completion):
                self.semaphore.signal()
                self.sendCompletion(completion)
            case .onCancelled:
                self.semaphore.signal()
                self.sendCancellation()
            }
            self.subscription?.request(.max(1))
            return nil
        })
        return .none
    }
    
    func receive(completion: Subscribers.Completion<Failure>) {
        semaphore.wait()
        subscription = nil
        guard let state else {
            self.state = .onComplete(completion: completion)
            semaphore.signal()
            sendCompletion(completion)
            return
        }
        switch state {
        case .onItem, .onComplete:
            self.state = .onComplete(completion: completion)
        case .onCancelled:
            break
        }
        semaphore.signal()
    }
    
    private func sendCompletion(_ completion: Subscribers.Completion<Failure>) {
        switch completion {
        case .finished:
            _ = onComplete(nil)
            break;
        case let .failure(error):
            _ = onComplete(error)
            break;
        }
    }
    
    func cancel() {
        semaphore.wait()
        subscription?.cancel()
        subscription = nil
        guard let state else {
            self.state = .onCancelled
            semaphore.signal()
            sendCancellation()
            return
        }
        switch state {
        case .onItem:
            self.state = .onCancelled
        case .onComplete, .onCancelled:
            break
        }
        semaphore.signal()
    }
    
    private func sendCancellation() {
        _ = onCancelled(CancellationError())
    }
}
