//
//  Cancellable.swift
//  KMPNativeCoroutinesCombine
//
//  Created by Rick Clephas on 02/09/2023.
//

import Combine
import KMPNativeCoroutinesCore

internal extension Cancellable {
    /// Creates a `NativeCancellable` for this `Cancellable`.
    func asNativeCancellable() -> NativeCancellable {
        return {
            self.cancel()
            return nil
        }
    }
}
