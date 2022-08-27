//
//  ObservablePublisher.swift
//  KMPNativeCoroutinesSwiftUI
//
//  Created by Rick Clephas on 27/08/2022.
//

import Combine
import Dispatch

@available(iOS 14.0, macOS 11.0, tvOS 14.0, watchOS 7.0, *)
internal class ObservablePublisher<Value>: ObservableObject {
    
    @Published private(set) var value: Value
    
    init<P: Publisher>(_ publisher: P, initialValue: Value) where P.Output == Value, P.Failure == Never {
        value = initialValue
        publisher.receive(on: DispatchQueue.main).assign(to: &$value)
    }
}
