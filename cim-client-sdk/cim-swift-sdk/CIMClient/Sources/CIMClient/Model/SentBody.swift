//
//  SentBody.swift
//  cimsdk
//
//  Created by FeiYu on 2021/9/27.
//

import Foundation

public struct SentBody: Codable {
    let key: String
    let timestamp: Int64
    let data: [String:String]
    
    public init(key: String, data: [String: String]) {
        self.key = key
        self.data = data
        self.timestamp = Date().currentTimestamp()
    }
    
    init(bytes: [UInt8]) {
        let sentBody = try! SentBodyProto.init(contiguousBytes: bytes)
        self.key = sentBody.key
        self.data = sentBody.data
        self.timestamp = sentBody.timestamp
    }
}

extension SentBody: Transportable {
    
    public var type: MessageType {
        get {
            return .sentBody(self)
        }
    }
    
    public var body: [UInt8] {
        get {
            var sentBody = SentBodyProto()
            sentBody.key = key
            sentBody.timestamp = timestamp
            data.forEach { k,v in
                sentBody.data[k] = v
            }
            let serializedData = try! sentBody.serializedData()
            let body: [UInt8] = serializedData.map{$0}
            return body
        }
    }
}
