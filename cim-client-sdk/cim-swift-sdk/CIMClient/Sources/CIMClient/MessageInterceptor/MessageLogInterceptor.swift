//
//  MessageLogInterceptor.swift
//  cimsdk
//
//  Created by FeiYu on 2021/10/1.
//

import Foundation
import Starscream

class MessageLogInterceptor: MessageInterceptor {
    
    var uniqueID: String = UUID().uuidString
    
    /// MARK: - ConnectListener
    func connect(_ event: ConnectEvent) {
        switch event {
        case .connecting:
            print("连接服务器。。。")
            break
        case .connected(let header):
            print("服务器连接成功！Header：\(header)")
            break
        case .disconnected(let reason, let code):
            print("与服务器断开连接。原因：\(reason) Code: \(code)")
            break
        case .error(let error):
            if let e = error as? WSError {
                print("websocket encountered an error: \(e.message)")
            } else if let e = error {
                print("websocket encountered an error: \(e.localizedDescription)")
            } else {
                print("websocket encountered an error")
            }
            break
        case .cancelled:
            print("取消连接")
            break
        case .bindUser(let success):
            print("用户绑定结果：\(success)")
            break
        }
    }
    
    /// MARK: - MessageListener
    func receiveMessage(_ message: Transportable) {
        print("Receive: \(message)")
    }
    
    func receiveMessageWithError(_ error: Error) {
        print("Receive Error: \(error)")
    }
    
    /// MARK:  - MessageSendListener
    func sendMessage(_ message: Transportable) {
        print("Sent: \(message)")
    }
}
