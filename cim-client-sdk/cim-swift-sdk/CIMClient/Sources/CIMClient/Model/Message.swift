//
//  Message.swift
//  cimsdk
//
//  Created by FeiYu on 2021/9/27.
//

import Foundation

public struct Message: Identifiable, Equatable, Codable {
    
    public let id: Int64
    public let action: String
    public let title: String
    public let content: String
    public let sender: String
    public let receiver: String
    public let format: String
    public let extra: String
    public let timestamp: Int64
    
    public init(action: String, sender: String, receiver: String, title: String?, content: String?, format: String?, extra: String?) {
        self.id = Date().currentTimestamp()
        self.action = action
        self.title = title ?? ""
        self.content = content ?? ""
        self.sender = sender
        self.receiver = receiver
        self.format = format ?? ""
        self.extra = extra ?? ""
        self.timestamp = Date().currentTimestamp()
    }
    
    init(bytes: [UInt8]) {
        let messageProto = try! MessageProto(contiguousBytes: bytes)
        self.id = messageProto.id
        self.action = messageProto.action
        self.title = messageProto.title
        self.content = messageProto.content
        self.sender = messageProto.sender
        self.receiver = messageProto.receiver
        self.format = messageProto.format
        self.extra = messageProto.extra
        self.timestamp = messageProto.timestamp
    }
}

extension Message: Transportable {
    
    public var type: MessageType {
        get {
            return .message(self)
        }
    }
    
    public var body: [UInt8] {
        get {
            var messageProto = MessageProto()
            messageProto.id = id
            messageProto.action = action
            messageProto.title = title
            messageProto.content = content
            messageProto.sender = sender
            messageProto.receiver = receiver
            messageProto.format = format
            messageProto.extra = extra
            messageProto.timestamp = timestamp
            let serializedData = try! messageProto.serializedData()
            let body: [UInt8] = serializedData.map{$0}
            return body
        }
    }
}
