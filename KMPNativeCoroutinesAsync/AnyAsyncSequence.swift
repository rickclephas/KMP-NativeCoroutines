//
//  AnyAsyncSequence.swift
//  KMPNativeCoroutinesAsync
//
//  Created by Rick Clephas on 02/09/2023.
//

/// An `AsyncSequence` that performs type erasure by wrapping another `AsyncSequence`.
public struct AnyAsyncSequence<Element>: AsyncSequence {
    public typealias Element = Element
    public typealias AsyncIterator = Iterator
    
    public struct Iterator: AsyncIteratorProtocol {
        
        private var base: any AsyncIteratorProtocol
        
        internal init<I: AsyncIteratorProtocol>(_ base: I) where I.Element == Element {
            self.base = base
        }
        
        public mutating func next() async throws -> Element? {
            return try await base.next() as! Element?
        }
    }
    
    private let _makeAsyncIterator: () -> Iterator
    
    internal init<S: AsyncSequence>(_ base: S) where S.Element == Element {
        _makeAsyncIterator = { Iterator(base.makeAsyncIterator()) }
    }
    
    public func makeAsyncIterator() -> Iterator {
        return _makeAsyncIterator()
    }
}
