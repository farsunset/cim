//
//  ConnectListener.swift
//  cimsdk
//
//  Created by FeiYu on 2021/10/1.
//

import Foundation

public protocol ConnectListener {
    
    var uniqueID: String { get }
    
    func connect(_ event: ConnectEvent)
}

extension ConnectListener {
    
    func connect(_ event: ConnectEvent) {
        
    }
}
