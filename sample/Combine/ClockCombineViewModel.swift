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
    
    func startMonitoring() {
        cancellable = createPublisher(for: clock.timeNative)
            .map { [weak self] time -> String in
                guard let self = self else { return "" }
                return self.formatter.string(from: Date(timeIntervalSince1970: time.doubleValue))
            }
            .replaceError(with: "Ohno error!")
            .receive(on: DispatchQueue.main)
            .sink { [weak self] time in
                guard let self = self else { return }
                self.time = time
            }
    }
    
    func stopMonitoring() {
        cancellable?.cancel()
        cancellable = nil
    }
}
