//
//  ClockCombineViewModel.swift
//  Sample
//
//  Created by Rick Clephas on 07/06/2021.
//

import Foundation
import Combine
import KMPNativeCoroutinesCombine
import NativeCoroutinesSampleShared

/// `ClockViewModel` implementation that uses Combine.
class ClockCombineViewModel: ClockViewModel {
    
    @Published private(set) var time: String = "--:--:--"
    @Published private(set) var isMonitoring: Bool = false
    
    private let formatter = { () -> DateFormatter in
        let formatter = DateFormatter()
        formatter.setLocalizedDateFormatFromTemplate("HH:mm:ss")
        return formatter
    }()
    private let clock = Clock()
    private var cancellable: AnyCancellable? = nil {
        didSet { isMonitoring = cancellable != nil }
    }
    
    private var timeline: Timeline? = nil
    
    func startMonitoring() {
        
        let database = Database()
        let locationHistoryFilter = LocationHistoryFilter(test: "test")
        let timelineTask = createPublisher(for: database.observeTimeline(locationHistoryFilter: locationHistoryFilter))
            .receive(on: DispatchQueue.main)
            .sink(
                receiveCompletion: { _ in },
                receiveValue: { [weak self] timeline in
                    self?.timeline = timeline
                }
            )
        
        cancellable = createPublisher(for: clock.time)
            // Convert the seconds since EPOCH to a string in the format "HH:mm:ss"
            .map { [weak self] time -> String in
                guard let self = self else { return "" }
                let date = Date(timeIntervalSince1970: time.doubleValue)
                return self.formatter.string(from: date)
            }
            // Replace any errors with a text message :)
            .replaceError(with: "Ohno error!")
            // Update the UI on the main thread
            .receive(on: DispatchQueue.main)
            .sink { [weak self] time in
                self?.time = time
            }
    }
    
    func stopMonitoring() {
        cancellable?.cancel()
        cancellable = nil
    }
    
    func updateTime() {
        // Convert the seconds since EPOCH to a string
        // in the format "HH:mm:ss" and update the UI
        let date = Date(timeIntervalSince1970: TimeInterval(clock.timeValue))
        time = formatter.string(from: date)
    }
}
