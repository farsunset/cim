//
//  BlurView.swift
//  CIMApp (macOS)
//
//  Created by FeiYu on 2021/10/15.
//

import SwiftUI

struct BlurView: NSViewRepresentable {
    
    
    func makeNSView(context: Context) -> NSVisualEffectView {
        let view = NSVisualEffectView()
        view.blendingMode = .behindWindow
        return view
    }
    
    func updateNSView(_ nsView: NSVisualEffectView, context: Context) {
        
    }
}
