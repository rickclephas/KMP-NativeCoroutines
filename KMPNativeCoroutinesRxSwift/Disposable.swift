//
//  Disposable.swift
//  KMPNativeCoroutinesRxSwift
//
//  Created by Rick Clephas on 02/09/2023.
//

import RxSwift
import KMPNativeCoroutinesCore

internal extension Disposable {
    /// Creates a `NativeCancellable` for this `Disposable`.
    func asNativeCancellable() -> NativeCancellable {
        return {
            self.dispose()
            return nil
        }
    }
}
