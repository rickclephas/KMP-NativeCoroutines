//
//  Task.swift
//  KMPNativeCoroutinesAsync
//
//  Created by Rick Clephas on 02/09/2023.
//

import KMPNativeCoroutinesCore

internal extension Task {
    /// Creates a `NativeCancellable` for this `Task`.
    func asNativeCancellable() -> NativeCancellable {
        return {
            self.cancel()
            return nil
        }
    }
}
