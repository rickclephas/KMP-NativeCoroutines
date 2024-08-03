//
//  Publisher.swift
//  KMPNativeCoroutinesCombine
//
//  Created by Rick Clephas on 06/06/2021.
//

import Combine
import Dispatch
import KMPNativeCoroutinesCore

internal let RETURN_TYPE_COMBINE_PUBLISHER = "combine-publisher"

/// Creates an `AnyPublisher` for the provided `NativeFlow`.
/// - Parameter nativeFlow: The native flow to collect.
/// - Returns: A publisher that publishes the collected values.
public func createPublisher<Output, Failure: Error>(
    for nativeFlow: @escaping NativeFlow<Output, Failure>
) -> AnyPublisher<Output, Failure> {
    if let publisher = nativeFlow(RETURN_TYPE_COMBINE_PUBLISHER, EmptyNativeCallback2, EmptyNativeCallback, EmptyNativeCallback)() {
        return publisher as! AnyPublisher<Output, Failure>
    }
    return NativeFlowPublisher(nativeFlow: nativeFlow).eraseToAnyPublisher()
}

/// Creates an `AnyPublisher` for the provided `NativeFlow`.
/// - Parameter nativeFlow: The native flow to collect.
/// - Returns: A publisher that publishes the collected values.
public func createPublisher<Unit, Failure: Error>(
    for nativeFlow: @escaping NativeFlow<Unit, Failure, Unit>
) -> AnyPublisher<Void, Failure> {
    return NativeFlowPublisher(nativeFlow: nativeFlow)
        .map { _ in }
        .eraseToAnyPublisher()
}

internal struct NativeFlowPublisher<Output, Failure: Error>: Publisher {
    
    typealias Output = Output
    typealias Failure = Failure
    
    let nativeFlow: NativeFlow<Output, Failure>
    
    func receive<S>(subscriber: S) where S : Subscriber, Failure == S.Failure, Output == S.Input {
        let subscription = NativeFlowSubscription(nativeFlow: nativeFlow, subscriber: subscriber)
        subscriber.receive(subscription: subscription)
    }
}

internal class NativeFlowSubscription<Output, Failure, S: Subscriber>: Subscription where S.Input == Output, S.Failure == Failure {
    
    private let semaphore = DispatchSemaphore(value: 1)
    private var nativeFlow: NativeFlow<Output, Failure>?
    private var nativeCancellable: NativeCancellable? = nil
    private var subscriber: S?
    private var demand: Subscribers.Demand = .none
    private var hasDemand: Bool { demand >= 1 }
    private var next: (() -> NativeUnit)? = nil
    
    init(nativeFlow: @escaping NativeFlow<Output, Failure>, subscriber: S) {
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
        nativeCancellable = nativeFlow(nil, { item, next, unit in
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
