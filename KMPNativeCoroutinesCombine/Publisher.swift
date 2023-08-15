//
//  Publisher.swift
//  KMPNativeCoroutinesCombine
//
//  Created by Rick Clephas on 06/06/2021.
//

import Combine
import Dispatch
import KMPNativeCoroutinesCore

/// Creates an `AnyPublisher` for the provided `NativeFlow`.
/// - Parameter nativeFlow: The native flow to collect.
/// - Returns: A publisher that publishes the collected values.
public func createPublisher<Output, Failure: Error, Unit>(
    for nativeFlow: @escaping NativeFlow<Output, Failure, Unit>
) -> AnyPublisher<Output, Failure> {
    return NativeFlowPublisher(nativeFlow: nativeFlow)
        .eraseToAnyPublisher()
}

internal struct NativeFlowPublisher<Output, Failure: Error, Unit>: Publisher {
    
    typealias Output = Output
    typealias Failure = Failure
    
    let nativeFlow: NativeFlow<Output, Failure, Unit>
    
    func receive<S>(subscriber: S) where S : Subscriber, Failure == S.Failure, Output == S.Input {
        let subscription = NativeFlowSubscription(nativeFlow: nativeFlow, subscriber: subscriber)
        subscriber.receive(subscription: subscription)
    }
}

internal class NativeFlowSubscription<Output, Failure, Unit, S: Subscriber>: Subscription where S.Input == Output, S.Failure == Failure {
    
    private let semaphore = DispatchSemaphore(value: 1)
    private var nativeFlow: NativeFlow<Output, Failure, Unit>?
    private var nativeCancellable: NativeCancellable<Unit>? = nil
    private var subscriber: S?
    private var demand: Subscribers.Demand = .none
    private var hasDemand: Bool { demand >= 1 }
    private var next: (() -> Unit)? = nil
    
    init(nativeFlow: @escaping NativeFlow<Output, Failure, Unit>, subscriber: S) {
        self.nativeFlow = nativeFlow
        self.subscriber = subscriber
    }
    
    func request(_ demand: Subscribers.Demand) {
        semaphore.wait()
        self.demand += demand
        guard hasDemand else {
            semaphore.signal()
            return
        }
        guard let nativeFlow = nativeFlow else {
            if let next = self.next {
                _ = next()
                self.next = nil
            }
            semaphore.signal()
            return
        }
        semaphore.signal()
        self.nativeFlow = nil
        nativeCancellable = nativeFlow({ item, next, unit in
            guard let subscriber = self.subscriber else { return unit }
            let demand = subscriber.receive(item)
            self.semaphore.wait()
            defer { self.semaphore.signal() }
            self.demand -= 1
            self.demand += demand
            if (self.hasDemand) {
                return next()
            } else {
                self.next = next
                return unit
            }
        }, { error, unit in
            if let error = error {
                self.subscriber?.receive(completion: .failure(error))
            } else {
                self.subscriber?.receive(completion: .finished)
            }
            return unit
        }, { cancellationError, unit in
            self.subscriber?.receive(completion: .failure(cancellationError))
            return unit
        })
    }
    
    func cancel() {
        subscriber = nil
        nativeFlow = nil
        _ = nativeCancellable?()
        nativeCancellable = nil
    }
}
