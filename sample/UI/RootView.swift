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
                NavigationLink(destination: ClockView(viewModel: ClockAsyncViewModel())) {
                    Text("Clock")
                }
                NavigationLink(destination: RandomLettersView(viewModel: RandomLettersAsyncViewModel())) {
                    Text("Random letters")
                }
                if #available(iOS 15.0, macOS 12.0, tvOS 15.0, watchOS 8.0, *) {
                    NavigationLink(destination: SwiftUIAsyncTest(tests: SuspendIntegrationTests())) {
                        Text("SwiftUI test")
                    }
                }
            }
            Section(header: Text("SwiftUI")) {
                if #available(iOS 14.0, macOS 11.0, tvOS 14.0, watchOS 7.0, *) {
                    NavigationLink(destination: StateFlowView(clock: Clock.shared)) {
                        Text("StateFlow")
                    }
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
