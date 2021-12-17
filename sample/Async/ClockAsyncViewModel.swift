//
//  ClockAsyncViewModel.swift
//  ClockAsyncViewModel
//
//  Created by Rick Clephas on 16/07/2021.
//

import Foundation
import KMPNativeCoroutinesAsync
import NativeCoroutinesSampleShared

/// `ClockViewModel` implementation that uses RxSwift.
class ClockAsyncViewModel: ClockViewModel {
    
    @Published private(set) var time: String = "--:--:--"
    @Published private(set) var isMonitoring: Bool = false
    
    private let formatter = { () -> DateFormatter in
        let formatter = DateFormatter()
        formatter.setLocalizedDateFormatFromTemplate("HH:mm:ss")
        return formatter
    }()
    private let clock = Clock()
    private var task: Task<(), Never>? = nil {
        didSet { isMonitoring = task != nil }
    }
    
    func startMonitoring() {
        task = Task { [weak self] in
            let timeStream = asyncStream(for: clock.timeNative)
                .map { [weak self] time -> String in
                    guard let self = self else { return "" }
                    let date = Date(timeIntervalSince1970: time.doubleValue)
                    return self.formatter.string(from: date)
                }
            do {
                for try await time in timeStream {
                    self?.time = time
                }
            } catch {
                // Replace any errors with a text message :)
                self?.time = "Ohno error!"
            }
            self?.task = nil
        }
    }
    
    func stopMonitoring() {
        task?.cancel()
        task = nil
    }
    
    func updateTime() {
        // Convert the seconds since EPOCH to a string
        // in the format "HH:mm:ss" and update the UI
        let date = Date(timeIntervalSince1970: TimeInterval(clock.timeNativeValue))
        time = formatter.string(from: date)
    }
}
