//
//  Extension.swift
//  CIMApp (macOS)
//
//  Created by FeiYu on 2021/10/16.
//

import SwiftUI

extension NSTextField {
    
    open override var focusRingType: NSFocusRingType {
        get{.none}
        set{}
    }
}

extension Int64 {
    
    //MARK: -时间戳转时间
    func timeStampToTime() -> String {
        let currentTime = Date().timeIntervalSince1970
        let timeSta: TimeInterval = TimeInterval(self / 1000)
        let reduceTime : TimeInterval = currentTime - timeSta
        let hours = Int(reduceTime / 3600)
        let date = NSDate(timeIntervalSince1970: timeSta)
        let dfmatter = DateFormatter()
        if hours < 24 {
            dfmatter.dateFormat="HH:mm"
            return dfmatter.string(from: date as Date)
        }
        let days = Int(reduceTime / 3600 / 24)
        if days < 365 {
            dfmatter.dateFormat="MM月dd日 HH:mm"
            return dfmatter.string(from: date as Date)
        }
        dfmatter.dateFormat="yyyy年MM月dd日 HH:mm:ss"
        return dfmatter.string(from: date as Date)
    }
}
