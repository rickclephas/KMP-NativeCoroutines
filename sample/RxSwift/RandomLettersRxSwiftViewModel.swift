//
//  RandomLettersRxSwiftViewModel.swift
//  Sample
//
//  Created by Rick Clephas on 09/07/2021.
//

import Foundation
import RxSwift
import KMPNativeCoroutinesRxSwift
import NativeCoroutinesSampleShared

/// `RandomLettersViewModel` implementation that uses RxSwift.
class RandomLettersRxSwiftViewModel: RandomLettersViewModel {
    
    @Published private(set) var result: Result<String, Error>? = nil
    @Published private(set) var isLoading: Bool = false
    
    private let randomLettersGenerator = RandomLettersGenerator.shared
    
    func loadRandomLetters(throwException: Bool) {
        isLoading = true
        result = nil
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let single = createSingle(for: { try await self.randomLettersGenerator.getRandomLettersNative(throwException: throwException) })
        #else
        let single = createSingle(for: randomLettersGenerator.getRandomLetters(throwException: throwException))
        #endif
            // Update the UI on the main thread
        _ = single.observe(on: MainScheduler.instance)
            .subscribe(onSuccess: { [weak self] word in
                self?.result = .success(word)
            }, onFailure: { [weak self] error in
                self?.result = .failure(error)
            }, onDisposed: { [weak self] in
                self?.isLoading = false
            })
    }
}
