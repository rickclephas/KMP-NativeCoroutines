//
//  SwiftUIAsyncTest.swift
//  Sample
//
//  Created by Rick Clephas on 15/06/2021.
//

import SwiftUI
import NativeCoroutinesSampleShared
import KMPNativeCoroutinesAsync

struct SwiftUIAsyncTest: View {
    
    var tests: SuspendIntegrationTests
    
    var body: some View {
        List {
            
        }.refreshable {
            print("Refreshable started")
            do {
                let result = try await asyncFunction(for: tests.returnValueNative(value: 20, delay: 10000))
                print("Refreshable result: \(result)")
            } catch {
                print("Refreshable error: \(error)")
            }
        }.task {
            print("Task started")
            do {
                let result = try await asyncFunction(for: tests.returnValueNative(value: 2, delay: 10000))
                print("Task result: \(result)")
            } catch {
                print("Task error: \(error)")
            }
        }
    }
    
}
