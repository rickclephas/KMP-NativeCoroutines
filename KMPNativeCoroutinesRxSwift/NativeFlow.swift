//
//  NativeFlow.swift
//  KMPNativeCoroutinesRxSwift
//
//  Created by Rick Clephas on 04/09/2023.
//

import RxSwift
import Dispatch
import KMPNativeCoroutinesCore

public extension Observable {
    /// Creates a `NativeFlow` for this `Observable`.
    func asNativeFlow() -> NativeFlow<Element, Error> {
        return { returnType, onItem, onComplete, onCancelled in
            if returnType == RETURN_TYPE_RXSWIFT_OBSERVABLE {
                return { self }
            } else if returnType != nil {
                return { nil }
            }
            let semaphore = DispatchSemaphore(value: 0)
            let disposable = self.subscribe(onNext: { value in
                _ = onItem(value, {
                    semaphore.signal()
                    return nil
                })
                semaphore.wait()
            }, onError: { error in
                _ = onComplete(error)
            }, onCompleted: {
                _ = onComplete(nil)
            })
            return {
                disposable.dispose()
                _ = onCancelled(DisposedError())
                return nil
            }
        }
    }
}

public extension Observable where Element == Void {
    /// Creates a `NativeFlow` for this `Observable`.
    func asNativeFlow() -> NativeFlow<NativeUnit?, Error> {
        map { nil }.asNativeFlow()
    }
}
