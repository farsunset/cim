//
//  Ping.swift
//  cimsdk
//
//  Created by FeiYu on 2021/9/27.
//

import Foundation

public struct Ping: Codable {
    let data: String
    public init() {
        self.data = "PING"
    }
}

extension Ping: Transportable {
    
    public var type: MessageType {
        get {
            return .ping(self)
        }
    }
    
    public var body: [UInt8] {
        get {
            let body: [UInt8] = Array(data.utf8)
            return body
        }
    }
}
