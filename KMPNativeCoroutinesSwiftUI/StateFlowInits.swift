//
//  StateFlowInits.swift
//  KMPNativeCoroutinesSwiftUI
//
//  Created by Rick Clephas on 27/08/2022.
//

import Combine
import KMPNativeCoroutinesCore
import KMPNativeCoroutinesCombine

@available(iOS 14.0, macOS 11.0, tvOS 14.0, watchOS 7.0, *)
public extension StateFlow {
    
    init<P: Publisher>(_ publisher: P, initialValue: Value) where P.Output == Value, P.Failure == Never {
        self.init(ObservablePublisher(publisher, initialValue: initialValue))
    }
    
    init<Failure: Error, Unit>(
        _ nativeFlow: @escaping NativeFlow<Value, Failure, Unit>,
        initialValue: Value
    ) {
        let publisher = createPublisher(for: nativeFlow).assertNoFailure()
        self.init(ObservablePublisher(publisher, initialValue: initialValue))
    }
    
    init<Failure: Error, Unit>(
        _ nativeFlow: @escaping NativeFlow<Value, Failure, Unit>,
        initialValue: Value,
        errorValue: Value
    ) {
        let publisher = createPublisher(for: nativeFlow).replaceError(with: errorValue)
        self.init(ObservablePublisher(publisher, initialValue: initialValue))
    }
    
    init<Output, Failure: Error, Unit>(
        _ nativeFlow: @escaping NativeFlow<Output, Failure, Unit>,
        initialValue: Value,
        transform: @escaping (Output) -> Value
    ) {
        let publisher = createPublisher(for: nativeFlow).map(transform).assertNoFailure()
        self.init(ObservablePublisher(publisher, initialValue: initialValue))
    }
    
    init<Output, Failure: Error, Unit>(
        _ nativeFlow: @escaping NativeFlow<Output, Failure, Unit>,
        initialValue: Value,
        errorValue: Value,
        transform: @escaping (Output) -> Value
    ) {
        let publisher = createPublisher(for: nativeFlow).map(transform).replaceError(with: errorValue)
        self.init(ObservablePublisher(publisher, initialValue: initialValue))
    }
    
    init<Output, Failure: Error, Unit>(
        _ nativeFlow: @escaping NativeFlow<Output, Failure, Unit>,
        initialValue: Value,
        errorValue: Output,
        transform: @escaping (Output) -> Value
    ) {
        let publisher = createPublisher(for: nativeFlow).map(transform).replaceError(with: transform(errorValue))
        self.init(ObservablePublisher(publisher, initialValue: initialValue))
    }
    
    init<Output, Failure: Error, Unit>(
        _ nativeFlow: @escaping NativeFlow<Output, Failure, Unit>,
        initialValue: Output,
        transform: @escaping (Output) -> Value
    ) {
        let publisher = createPublisher(for: nativeFlow).map(transform).assertNoFailure()
        self.init(ObservablePublisher(publisher, initialValue: transform(initialValue)))
    }
    
    init<Output, Failure: Error, Unit>(
        _ nativeFlow: @escaping NativeFlow<Output, Failure, Unit>,
        initialValue: Output,
        errorValue: Output,
        transform: @escaping (Output) -> Value
    ) {
        let publisher = createPublisher(for: nativeFlow).map(transform).replaceError(with: transform(errorValue))
        self.init(ObservablePublisher(publisher, initialValue: transform(initialValue)))
    }
    
    init<Output, Failure: Error, Unit>(
        _ nativeFlow: @escaping NativeFlow<Output, Failure, Unit>,
        initialValue: Output,
        errorValue: Value,
        transform: @escaping (Output) -> Value
    ) {
        let publisher = createPublisher(for: nativeFlow).map(transform).replaceError(with: errorValue)
        self.init(ObservablePublisher(publisher, initialValue: transform(initialValue)))
    }
}
