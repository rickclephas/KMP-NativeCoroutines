//
//  NativeCallback.swift
//  KMPNativeCoroutinesCore
//
//  Created by Rick Clephas on 06/06/2021.
//

/// A callback with no arguments.
public typealias NativeCallback = () -> NativeUnit?

/// A callback with a single argument.
public typealias NativeCallback1<T> = (T) -> NativeUnit?

/// A no-op function that has the signature of a `NativeCallback1`.
public func EmptyNativeCallback1<T>(value: T) -> NativeUnit? { nil }

/// A callback with two arguments.
public typealias NativeCallback2<T1, T2> = (T1, T2) -> NativeUnit?

/// A no-op function that has the signature of a `NativeCallback2`.
public func EmptyNativeCallback2<T1, T2>(value1: T1, value2: T2) -> NativeUnit? { nil }
