//
//  StateFlow.swift
//  KMPNativeCoroutinesSwiftUI
//
//  Created by Rick Clephas on 27/08/2022.
//

import SwiftUI

@available(iOS 14.0, macOS 11.0, tvOS 14.0, watchOS 7.0, *)
@propertyWrapper
public struct StateFlow<Value>: DynamicProperty {
    
    @ObservedObject private var observablePublisher: ObservablePublisher<Value>
    
    internal init(_ observablePublisher: ObservablePublisher<Value>) {
        self.observablePublisher = observablePublisher
    }
    
    public var wrappedValue: Value { observablePublisher.value }
}
