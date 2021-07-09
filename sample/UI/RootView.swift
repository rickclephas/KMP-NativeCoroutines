//
//  RootView.swift
//  Sample
//
//  Created by Rick Clephas on 09/06/2021.
//

import SwiftUI
import NativeCoroutinesSampleShared

struct RootView: View {
    var body: some View {
        List {
            Section(header: Text("Combine")) {
                NavigationLink(destination: ClockView(viewModel: ClockCombineViewModel())) {
                    Text("Clock")
                }
                NavigationLink(destination: RandomLettersView(viewModel: RandomLettersCombineViewModel())) {
                    Text("Random letters")
                }
            }
            Section(header: Text("RxSwift")) {
                NavigationLink(destination: ClockView(viewModel: ClockRxSwiftViewModel())) {
                    Text("Clock")
                }
                NavigationLink(destination: RandomLettersView(viewModel: RandomLettersRxSwiftViewModel())) {
                    Text("Random letters")
                }
            }
            Section(header: Text("Async/Await")) {
                NavigationLink(destination: RandomLettersView(viewModel: RandomLettersAsyncViewModel())) {
                    Text("Random letters")
                }
                NavigationLink(destination: SwiftUIAsyncTest(tests: SuspendIntegrationTests())) {
                    Text("SwiftUI test")
                }
            }
        }.navigationBarTitle(inlineTitle: "KMP-NativeCoroutines")
    }
}

struct RootView_Previews: PreviewProvider {
    static var previews: some View {
        RootView().previewDevice("iPhone 12 Pro Max")
    }
}
