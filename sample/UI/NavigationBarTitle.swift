//
//  NavigationBarTitle.swift
//  Sample
//
//  Created by Rick Clephas on 09/06/2021.
//

import SwiftUI

#if os(iOS)
extension View {
    func navigationBarTitle(inlineTitle title: String) -> some View {
        return navigationBarTitle(Text(title), displayMode: .inline)
    }
}
#else
extension View {
    func navigationBarTitle(inlineTitle title: String) -> some View {
        return self
    }
}
#endif
