//
//  RandomLettersAsyncViewModel.swift
//  Sample
//
//  Created by Rick Clephas on 13/06/2021.
//

import Foundation
import KMPNativeCoroutinesAsync
import NativeCoroutinesSampleShared

/// `RandomLettersViewModel` implementation that uses Swifts Async/Await.
class RandomLettersAsyncViewModel: RandomLettersViewModel {
    
    @Published private(set) var result: Result<String, Error>? = nil
    @Published private(set) var isLoading: Bool = false
    
    private let randomLettersGenerator = RandomLettersGenerator.shared
    
    func loadRandomLetters(throwException: Bool) {
        #if !NATIVE_COROUTINES_SWIFT_EXPORT
        Task {
            isLoading = true
            result = nil
            do {
                let letters = try await asyncFunction(for: randomLettersGenerator.getRandomLetters(throwException: throwException))
                result = .success(letters)
            } catch {
                result = .failure(error)
            }
            isLoading = false
        }
        #endif
    }
}
