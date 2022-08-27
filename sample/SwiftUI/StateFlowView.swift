//
//  StateFlowView.swift
//  KMPNativeCoroutinesSample
//
//  Created by Rick Clephas on 27/08/2022.
//

import Combine
import KMPNativeCoroutinesCore
import KMPNativeCoroutinesCombine
import KMPNativeCoroutinesSwiftUI
import NativeCoroutinesSampleShared
import SwiftUI

private let timeFormatter = { () -> DateFormatter in
    let formatter = DateFormatter()
    formatter.setLocalizedDateFormatFromTemplate("HH:mm:ss")
    return formatter
}()

@available(iOS 14.0, macOS 11.0, tvOS 14.0, watchOS 7.0, *)
struct StateFlowView: View {
    
    @StateFlow var time: String
    
    init(clock: Clock) {
        _time = StateFlow(clock.timeNative, initialValue: KotlinLong(value: clock.timeNativeValue)) { time in
            timeFormatter.string(from: Date(timeIntervalSince1970: time.doubleValue))
        }
    }
    
    var body: some View {
        Text(time).font(.system(size: 30, weight: .bold, design: .monospaced))
    }
}
