//
//  ClockRxSwiftViewModel.swift
//  Sample
//
//  Created by Rick Clephas on 09/07/2021.
//

import Foundation
import RxSwift
import KMPNativeCoroutinesRxSwift
import NativeCoroutinesSampleShared

/// `ClockViewModel` implementation that uses RxSwift.
class ClockRxSwiftViewModel: ClockViewModel {
    
    @Published private(set) var time: String = "--:--:--"
    @Published private(set) var isMonitoring: Bool = false
    
    private let formatter = { () -> DateFormatter in
        let formatter = DateFormatter()
        formatter.setLocalizedDateFormatFromTemplate("HH:mm:ss")
        return formatter
    }()
    private let clock = Clock()
    private var disposable: Disposable? = nil {
        didSet { isMonitoring = disposable != nil }
    }
    
    func startMonitoring() {
        disposable = createObservable(for: clock.time)
            // Convert the seconds since EPOCH to a string in the format "HH:mm:ss"
            .map { [weak self] time -> String in
                guard let self = self else { return "" }
                let date = Date(timeIntervalSince1970: time.doubleValue)
                return self.formatter.string(from: date)
            }
            // Update the UI on the main thread
            .observe(on: MainScheduler.instance)
            .subscribe(onNext: { [weak self] time in
                self?.time = time
            }, onError: { [weak self] _ in
                // Replace any errors with a text message :)
                self?.time = "Ohno error!"
            })
    }
    
    func stopMonitoring() {
        disposable?.dispose()
        disposable = nil
    }
    
    func updateTime() {
        // Convert the seconds since EPOCH to a string
        // in the format "HH:mm:ss" and update the UI
        let date = Date(timeIntervalSince1970: TimeInterval(clock.timeValue))
        time = formatter.string(from: date)
    }
}
