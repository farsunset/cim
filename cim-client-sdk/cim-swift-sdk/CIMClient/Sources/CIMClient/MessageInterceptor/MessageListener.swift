//
//  MessageListener.swift
//  cimsdk
//
//  Created by FeiYu on 2021/10/1.
//

import Foundation

public protocol MessageListener {
    
    var uniqueID: String { get }
    
    func receiveMessage(_ message: Transportable)
    
    func receiveMessageWithError(_ error: Error)
}

public protocol MessageSendListener {
    
    func sendMessage(_ message: Transportable)
}

extension MessageListener {
    
    func receiveMessage(_ message: Transportable) {
        
    }
    
    func receiveMessageWithError(_ error: Error) {
        
    }
}

extension MessageSendListener {
    
    func sendMessage(_ message: Transportable) {
        
    }
}
