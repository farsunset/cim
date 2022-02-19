//
//  Constant.swift
//  cimsdk
//
//  Created by FeiYu on 2021/9/27.
//

import Foundation

/// 消息类型
public enum MessageType {
    
    public enum Key: UInt8 {
        case pong = 0
        case ping = 1
        case message = 2
        case sentBody = 3
        case replyBody = 4
    }
    
    case pong(Pong)
    case ping(Ping)
    case message(Message)
    case sentBody(SentBody)
    case replyBody(ReplyBody)
    
    var bytes: [UInt8] {
        switch self {
        case .pong: return [Key.pong.rawValue]
        case .ping: return [Key.ping.rawValue]
        case .message: return [Key.message.rawValue]
        case .sentBody: return [Key.sentBody.rawValue]
        case .replyBody: return [Key.replyBody.rawValue]
        }
    }
}

public enum ConnectEvent {
    case connecting
    case connected([String: String])
    case disconnected(String, UInt16)
    case error(Error?)
    case cancelled
    case bindUser(Bool)
}

let sdkVersion: String = "cimsdk-ios-1.0.0"

