//
//  Pong.swift
//  cimsdk
//
//  Created by FeiYu on 2021/9/27.
//

import Foundation

public struct Pong: Codable {
    let data: String
    public init() {
        self.data = "PONG"
    }
}

extension Pong: Transportable {
    
    public var type: MessageType {
        get {
            return .pong(self)
        }
    }
    
    public var body: [UInt8] {
        get {
            let body: [UInt8] = Array(data.utf8)
            return body
        }
    }
    
}
