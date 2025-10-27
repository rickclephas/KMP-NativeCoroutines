//
//  Publisher.swift
//  KMPNativeCoroutinesCombine
//
//  Created by Rick Clephas on 06/06/2021.
//

import Combine
import Dispatch
import KMPNativeCoroutinesCore
import Foundation

/// Creates an `AnyPublisher` for the provided `NativeFlow`.
/// - Parameter nativeFlow: The native flow to collect.
/// - Returns: A publisher that publishes the collected values.
public func createPublisher<Output, Failure: Error, Unit>(
    for nativeFlow: @escaping NativeFlow<Output, Failure, Unit>
) -> AnyPublisher<Output, Failure> {
    return NativeFlowPublisher(nativeFlow: nativeFlow)
        .eraseToAnyPublisher()
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

internal struct NativeFlowPublisher<Output, Failure: Error, Unit>: Publisher {
    
    typealias Output = Output
    typealias Failure = Failure
    
    let nativeFlow: NativeFlow<Output, Failure, Unit>
    
    func receive<S>(subscriber: S) where S : Subscriber, Failure == S.Failure, Output == S.Input {
        let subscription = NativeFlowSubscription(nativeFlow: nativeFlow, subscriber: subscriber)
        subscriber.receive(subscription: subscription)
    }
}

internal final class NativeFlowSubscription<Output, Failure, Unit, S: Subscriber>: Subscription
where S.Input == Output, S.Failure == Failure {

  private let lock = NSLock()
  private var nativeFlow: NativeFlow<Output, Failure, Unit>?
  private var nativeCancellable: NativeCancellable<Unit>?
  private var subscriber: S?
  private var demand: Subscribers.Demand = .none
  private var pendingNext: (() -> Unit)?
  private var finished = false
  private var cancelled = false

  init(nativeFlow: @escaping NativeFlow<Output, Failure, Unit>, subscriber: S) {
    self.nativeFlow = nativeFlow
    self.subscriber = subscriber
  }

  func request(_ newDemand: Subscribers.Demand) {
    var toStartFlow: NativeFlow<Output, Failure, Unit>?
    var toResumeNext: (() -> Unit)?

    lock.lock()
    guard !cancelled, !finished else {
      lock.unlock()
      return
    }

    demand += newDemand

    if demand > .none, let nxt = pendingNext {
      toResumeNext = nxt
      pendingNext = nil
    }

    if let nf = nativeFlow {
      toStartFlow = nf
      nativeFlow = nil
    }
    lock.unlock()

    // Execute callbacks outside the lock to prevent deadlocks
    // when callbacks synchronously invoke completion/error handlers
    if let resume = toResumeNext {
      _ = resume()
    }

    if let flow = toStartFlow {
      start(flow: flow)
    }
  }

  private func start(flow: @escaping NativeFlow<Output, Failure, Unit>) {
    nativeCancellable = flow(
      { [weak self] item, next, unit in
        guard let self else { return unit }

        var deliverTo: S?
        var callNext: (() -> Unit)?

        self.lock.lock()
        if self.cancelled || self.finished || self.subscriber == nil {
          self.lock.unlock()
          return unit
        }

        if self.demand == .none {
          self.pendingNext = next
          self.lock.unlock()
          return unit
        }

        self.demand -= 1
        deliverTo = self.subscriber
        self.lock.unlock()

        let more = deliverTo?.receive(item) ?? .none

        self.lock.lock()
        self.demand += more
        if self.demand > .none {
          callNext = next
        } else {
          self.pendingNext = next
        }
        self.lock.unlock()

        if let callNext = callNext {
          return callNext()
        } else {
          return unit
        }
      }, { [weak self] maybeError, unit in
        guard let self else { return unit }

        var finishWith: Subscribers.Completion<Failure>?
        var toNotify: S?

        self.lock.lock()
        if !self.cancelled && !self.finished {
          toNotify = self.subscriber
          self.subscriber = nil
          self.finished = true
          finishWith = (maybeError != nil) ? .failure(maybeError!) : .finished
        }
        self.lock.unlock()

        if let sub = toNotify, let completion = finishWith {
          sub.receive(completion: completion)
        }
        return unit
      }, { [weak self] cancellationError, unit in
        guard let self else { return unit }

        var toNotify: S?

        self.lock.lock()
        if !self.cancelled && !self.finished {
          toNotify = self.subscriber
          self.subscriber = nil
          self.finished = true
        }
        self.lock.unlock()

        if let sub = toNotify {
          sub.receive(completion: .failure(cancellationError))
        }
        return unit
      }
    )
  }

  func cancel() {
    var toCancel: NativeCancellable<Unit>?

    lock.lock()
    if cancelled {
      lock.unlock()
      return
    }
    cancelled = true
    toCancel = nativeCancellable
    nativeCancellable = nil
    subscriber = nil
    pendingNext = nil
    nativeFlow = nil
    lock.unlock()

    _ = toCancel?()
  }
}
