//
//  RandomWordCombineViewModel.swift
//  Sample
//
//  Created by Rick Clephas on 09/06/2021.
//

import Foundation
import Combine
import KMPNativeCoroutinesCombine
import NativeCoroutinesSampleShared

/// `RandomLettersViewModel` implementation that uses Combine.
class RandomLettersCombineViewModel: RandomLettersViewModel {
    
    @Published private(set) var result: Result<String, Error>? = nil
    @Published private(set) var isLoading: Bool = false
    
    private let randomLettersGenerator = RandomLettersGenerator()
    private var cancellables = Set<AnyCancellable>()
    
    func loadRandomLetters(throwException: Bool) {
        isLoading = true
        result = nil
        #if !NATIVE_COROUTINES_SWIFT_EXPORT
        createFuture(for: randomLettersGenerator.getRandomLetters(throwException: throwException))
            // Update the UI on the main thread
            .receive(on: DispatchQueue.main)
            .sink { [weak self] completion in
                if case let .failure(error) = completion {
                    self?.result = .failure(error)
                }
                self?.isLoading = false
            } receiveValue: { [weak self] word in
                self?.result = .success(word)
            }.store(in: &cancellables)
        #endif
    }
}
