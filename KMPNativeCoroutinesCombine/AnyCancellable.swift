//
//  AnyCancellable.swift
//  KMPNativeCoroutinesCombine
//
//  Created by Rick Clephas on 02/09/2023.
//

import Combine
import KMPNativeCoroutinesCore

internal extension AnyCancellable {
    /// Creates a `NativeCancellable` for this `AnyCancellable`.
    func asNativeCancellable() -> NativeCancellable {
        return {
            self.cancel()
            return nil
        }
    }
}
