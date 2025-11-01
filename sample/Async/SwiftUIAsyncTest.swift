//
//  SwiftUIAsyncTest.swift
//  Sample
//
//  Created by Rick Clephas on 15/06/2021.
//

import SwiftUI
import NativeCoroutinesSampleShared
import KMPNativeCoroutinesAsync

@available(iOS 15.0, macOS 12.0, tvOS 15.0, watchOS 8.0, *)
struct SwiftUIAsyncTest: View {
    
    var tests: SuspendIntegrationTests
    
    var body: some View {
        List {
            
        }.refreshable {
            print("Refreshable started")
            #if !NATIVE_COROUTINES_SWIFT_EXPORT
            do {
                let result = try await asyncFunction(for: tests.returnValue(value: 20, delay: 10000))
                print("Refreshable result: \(result)")
            } catch {
                print("Refreshable error: \(error)")
            }
            #endif
        }.task {
            print("Task started")
            #if !NATIVE_COROUTINES_SWIFT_EXPORT
            do {
                let result = try await asyncFunction(for: tests.returnValue(value: 2, delay: 10000))
                print("Task result: \(result)")
            } catch {
                print("Task error: \(error)")
            }
            #endif
        }
    }
    
}
