//
//  ClockView.swift
//  Sample
//
//  Created by Rick Clephas on 07/06/2021.
//

import SwiftUI

struct ClockView<ViewModel: ClockViewModel>: View {
    
    @ObservedObject var viewModel: ViewModel
    
    private var isMonitoringBinding: Binding<Bool> {
        Binding { viewModel.isMonitoring } set: { isMonitoring in
            if !isMonitoring && viewModel.isMonitoring {
                viewModel.stopMonitoring()
            } else if isMonitoring && !viewModel.isMonitoring {
                viewModel.startMonitoring()
            }
        }
    }
    
    var body: some View {
        VStack{
            Spacer()
            Spacer()
            Text(viewModel.time)
                .font(.system(size: 50, weight: .bold, design: .monospaced))
            Spacer()
            Text("Monitor time")
            Toggle("", isOn: isMonitoringBinding)
                .labelsHidden()
            Spacer()
            Button("Update time") {
                viewModel.updateTime()
            }.disabled(viewModel.isMonitoring)
            Spacer()
        }.navigationBarTitle(inlineTitle: "Clock")
    }
}

struct ClockView_Previews: PreviewProvider {
    static var previews: some View {
        ClockView(viewModel: ClockPreviewViewModel())
            .previewDevice("iPhone 12 Pro Max")
    }
}
