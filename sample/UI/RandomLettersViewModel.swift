//
//  RandomWordViewModel.swift
//  Sample
//
//  Created by Rick Clephas on 09/06/2021.
//

import Foundation

/// A type of object that is used as the view model for the `RandomLettersView`.
protocol RandomLettersViewModel: ObservableObject {
    /// The result of the `loadRandomLetters` action.
    var result: Result<String, Error>? { get }
    /// Indicates if random letters are being loaded.
    var isLoading: Bool { get }
    
    /// Loads random letters or throws an exception
    func loadRandomLetters(throwException: Bool)
}

/// `RandomLettersViewModel` implementation for the SwiftUI previews.
class RandomLettersPreviewViewModel: RandomLettersViewModel {
    let result: Result<String, Error>?
    var isLoading: Bool
    
    init(result: Result<String, Error>? = nil, isLoading: Bool = false) {
        self.result = result
        self.isLoading = isLoading
    }
    
    func loadRandomLetters(throwException: Bool) { }
}
