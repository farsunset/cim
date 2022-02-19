//
//  Date+Extension.swift
//  cimsdk
//
//  Created by FeiYu on 2021/9/27.
//

import Foundation

extension Date {
    
    func currentTimestamp() -> Int64 {
        let timeInterval: TimeInterval = Date().timeIntervalSince1970 * 1000
        let timestamp = Int64(timeInterval)
        return timestamp
    }
}
