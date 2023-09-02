//
//  NativeCallback.swift
//  KMPNativeCoroutinesCore
//
//  Created by Rick Clephas on 06/06/2021.
//

/// A callback with a single argument.
///
/// The return value is provided as the second argument.
/// This way Swift doesn't known what it is/how to get it.
public typealias NativeCallback<T> = (T, NativeUnit) -> NativeUnit

/// A no-op functions that has the signature of a `NativeCallback`.
public func EmptyNativeCallback<T>(value: T, unit: NativeUnit) -> NativeUnit { unit }

/// A callback with two arguments.
///
/// The return value is provided as the third argument.
/// This way Swift doesn't known what it is/how to get it.
public typealias NativeCallback2<T1, T2> = (T1, T2, NativeUnit) -> NativeUnit

/// A no-op functions that has the signature of a `NativeCallback2`.
public func EmptyNativeCallback2<T1, T2>(value1: T1, value2: T2, unit: NativeUnit) -> NativeUnit { unit }
