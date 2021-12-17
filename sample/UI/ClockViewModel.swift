//
//  ClockViewModel.swift
//  Sample
//
//  Created by Rick Clephas on 07/06/2021.
//

import Foundation

/// A type of object that is used as the view model for the `ClockView`.
@MainActor
protocol ClockViewModel: ObservableObject {
    /// The current time formatted as "HH:mm:ss".
    var time: String { get }
    /// Indicates if the time is currently being monitored.
    var isMonitoring: Bool { get }
    
    /// Start monitoring the time.
    func startMonitoring()
    /// Stops monitoring the time.
    func stopMonitoring()
    /// Update the `time` value to the current time.
    func updateTime()
}

/// `ClockViewModel` implementation for the SwiftUI previews.
class ClockPreviewViewModel: ClockViewModel {
    let time: String
    let isMonitoring: Bool
    
    init(time: String = "20:08:58", isMonitoring: Bool = false) {
        self.time = time
        self.isMonitoring = isMonitoring
    }
    
    func startMonitoring() { }
    func stopMonitoring() { }
    func updateTime() { }
}
