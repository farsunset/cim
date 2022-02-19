//
//  ReplyBody.swift
//  cimsdk
//
//  Created by FeiYu on 2021/9/27.
//

import Foundation

public struct ReplyBody: Codable {
    
    let key: String
    let code: String
    let message: String?
    let data: [String: String]?
    let timestamp: Int64
    
    public init(key: String, code: String, message: String?, data: [String: String]?) {
        self.key = key
        self.code = code
        self.message = message
        self.data = data
        self.timestamp = Date().currentTimestamp()
    }
    
    init(bytes: [UInt8]) {
        let replyBody = try! ReplayBodyProto.init(contiguousBytes: bytes)
        self.key = replyBody.key
        self.code = replyBody.code
        self.message = replyBody.message
        self.data = replyBody.data
        self.timestamp = replyBody.timestamp
    }
}

extension ReplyBody: Transportable {
    
    public var type: MessageType {
        get {
            return .replyBody(self)
        }
    }
    
    public var body: [UInt8] {
        get {
            var replyBody = ReplayBodyProto()
            replyBody.key = key
            replyBody.code = code
            if let msg: String = message {
                replyBody.message = msg
            }
            if let data = data {
                data.forEach { k, v in
                    replyBody.data[k] = v
                }
            }
            replyBody.timestamp = timestamp
            let serializedData = try! replyBody.serializedData()
            let body: [UInt8] = serializedData.map{$0}
            return body
        }
    }
}
