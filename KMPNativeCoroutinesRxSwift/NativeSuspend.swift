//
//  NativeSuspend.swift
//  KMPNativeCoroutinesRxSwift
//
//  Created by Rick Clephas on 04/09/2023.
//

import RxSwift
import KMPNativeCoroutinesCore

public extension Single where Trait == SingleTrait {
    /// Creates a `NativeSuspend` for this `Single`.
    func asNativeSuspend() -> NativeSuspend<Element, Error> {
        return { returnType, onResult, onError, onCancelled in
            if returnType == RETURN_TYPE_RXSWIFT_SINGLE {
                return { self }
            } else if returnType != nil {
                return { nil }
            }
            let disposable = self.subscribe(onSuccess: { value in
                _ = onResult(value)
            }, onFailure: { error in
                _ = onError(error)
            })
            return {
                disposable.dispose()
                _ = onCancelled(DisposedError())
                return nil
            }
        }
    }
}
