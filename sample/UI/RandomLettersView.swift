//
//  RandomWordView.swift
//  Sample
//
//  Created by Rick Clephas on 09/06/2021.
//

import SwiftUI

struct RandomLettersView<ViewModel: RandomLettersViewModel>: View {
    
    @ObservedObject var viewModel: ViewModel
    
    var body: some View {
        VStack {
            Spacer()
            Spacer()
            Group {
                if viewModel.isLoading {
                    Text("Loading..\n")
                } else if case let .success(word) = viewModel.result {
                    Text("Letters:\n\(word)")
                } else if case let .failure(error) = viewModel.result {
                    Text("Error:\n\(error.localizedDescription)")
                }
            }
            .font(.system(size: 35, weight: .bold))
            .multilineTextAlignment(.center)
            Spacer()
            Button("Random letters") {
                viewModel.loadRandomLetters(throwException: false)
            }.disabled(viewModel.isLoading)
            Spacer()
            Button("Throw exception") {
                viewModel.loadRandomLetters(throwException: true)
            }.disabled(viewModel.isLoading)
            Spacer()
        }.navigationBarTitle(inlineTitle: "Random letters")
    }
}

struct RandomLettersView_Previews: PreviewProvider {
    static var previews: some View {
        RandomLettersView(viewModel: RandomLettersPreviewViewModel(isLoading: true))
            .previewDevice("iPhone 12 Pro Max")
    }
}
