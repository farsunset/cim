//
//  MessageSender.swift
//  cimsdk
//
//  Created by FeiYu on 2021/10/2.
//

import Foundation

public protocol MessageSender {
    
    func sendTextMessage(_ text: String, receiver: String)
}

class MessageSenderImp: MessageSender {
    
    weak var client: CIMClient?
    
    init(client: CIMClient) {
        self.client = client
    }
    
    func sendTextMessage(_ text: String, receiver: String) {
        if let client = client {
            if let account = client.account {
                let msg = MessageBuilder()
                    .set(type: .message)
                    .set(action: "2")
                    .set(sender: String(account.id))
                    .set(receiver: receiver)
                    .set(title: "title")
                    .set(content: text)
                    .set(format: "")
                    .set(extra: "")
                    .build()
                if let msg = msg {
                    client.sendMessage(msg)
                }
            }
        }
    }
}
