//
//  MessageFactory.swift
//  cimsdk
//
//  Created by FeiYu on 2021/9/29.
//

import Foundation

public class MessageBuilder {
    /// Type
    private var type: MessageType.Key?
    /// Message
    private var action: String = String()
    private var title: String = String()
    private var content: String = String()
    private var sender: String = String()
    private var receiver: String = String()
    private var format: String = String()
    private var extra: String = String()
    /// Reply & Sent
    private var key: String = String()
    private var data: [String:String]? = [:]
    /// Reply
    private var code: String = String()
    private var message: String? = String()
    
    /// Transportable Data
    private var transportData: Data = Data()
    
    
    public func set(type: MessageType.Key) -> MessageBuilder {
        self.type = type
        return self
    }
    
    public func set(action: String) -> MessageBuilder {
        self.action = action
        return self
    }
    
    public func set(title: String) -> MessageBuilder {
        self.title = title
        return self
    }
    
    public func set(content: String) -> MessageBuilder {
        self.content = content
        return self
    }
    
    public func set(sender: String) -> MessageBuilder {
        self.sender = sender
        return self
    }
    
    public func set(receiver: String) -> MessageBuilder {
        self.receiver = receiver
        return self
    }
    
    public func set(format: String) -> MessageBuilder {
        self.format = format
        return self
    }
    
    public func set(extra: String) -> MessageBuilder {
        self.extra = extra
        return self
    }
    
    public func set(key: String) -> MessageBuilder {
        self.key = key
        return self
    }
    
    public func set(data: [String: String]) -> MessageBuilder {
        self.data = data
        return self
    }
    
    public func set(code: String) -> MessageBuilder {
        self.code = code
        return self
    }
    
    public func set(message: String) -> MessageBuilder {
        self.message = message
        return self
    }
    
    public func set(transportData: Data) -> MessageBuilder {
        self.transportData = transportData
        return self
    }
    
    public func build() -> Transportable? {
        switch type {
        case .pong: return Pong()
        case .ping: return Ping()
        case .message: return Message(
            action: action,
            sender: sender,
            receiver: receiver,
            title: title,
            content: content,
            format: format,
            extra: extra)
        case .sentBody: return SentBody(key: key, data: data ?? [:])
        case .replyBody: return ReplyBody(key: key, code: code, message: message, data: data)
        case .none:
            let data = transportData
            if data.count > 3 {
                let bytes: [UInt8] = data.map{$0}
                let type = MessageType.Key(rawValue: bytes[0])
                let body: [UInt8] = bytes[1..<bytes.count].map{$0}
                switch type {
                case .ping: return Ping()
                case .pong: return Pong()
                case .message: return Message(bytes: body)
                case .sentBody: return SentBody(bytes: body)
                case .replyBody: return ReplyBody(bytes: body)
                default: return nil
                }
            } else {
                return nil
            }
        }
    }
    
}
