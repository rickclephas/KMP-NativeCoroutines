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
    
    private let randomLettersGenerator = RandomLettersGenerator()
    
    func loadRandomLetters(throwException: Bool) {
        isLoading = true
        result = nil
        _ = createSingle(for: randomLettersGenerator.getRandomLetters(throwException: throwException))
            // Update the UI on the main thread
            .observe(on: MainScheduler.instance)
            .subscribe(onSuccess: { [weak self] word in
                self?.result = .success(word)
            }, onFailure: { [weak self] error in
                self?.result = .failure(error)
            }, onDisposed: { [weak self] in
                self?.isLoading = false
            })
    }
}
