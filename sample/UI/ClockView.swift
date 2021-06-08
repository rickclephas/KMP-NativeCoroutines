//
//  ClockView.swift
//  Sample
//
//  Created by Rick Clephas on 07/06/2021.
//

import SwiftUI

struct ClockView<ViewModel: ClockViewModel>: View {
    
    @ObservedObject var viewModel: ViewModel
    
    var body: some View {
        VStack{
            Spacer()
            Spacer()
            Text(viewModel.time)
                .font(.system(size: 50, weight: .bold, design: .monospaced))
            Spacer()
            Button(viewModel.isMonitoring ? "Stop" : "Start") {
                if viewModel.isMonitoring {
                    viewModel.stopMonitoring()
                } else {
                    viewModel.startMonitoring()
                }
            }
            .font(.system(size: 25, weight: .regular, design: .rounded))
            .buttonStyle(BorderlessButtonStyle())
            Spacer()
        }
    }
}

struct ClockView_Previews: PreviewProvider {
    static var previews: some View {
        ClockView(viewModel: ClockPreviewViewModel())
            .previewDevice("iPhone 12 Pro Max")
    }
}
