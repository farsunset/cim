//
//  AutoReConnectInterceptor.swift
//  cimsdk
//
//  Created by FeiYu on 2021/10/1.
//

import Foundation

class AutoReConnectInterceptor: MessageInterceptor {
    
    var uniqueID: String = UUID().uuidString
    
    weak var client: CIMClient?
    
    init(client: CIMClient) {
        self.client = client
    }
    
    func connect(_ event: ConnectEvent) {
        switch event {
        case .connected(_):
            if let account = self.client?.account {
                let msg = MessageBuilder()
                    .set(type: .sentBody)
                    .set(key: "client_bind")
                    .set(data: [
                        "uid": String(account.id),
                        "channel": "ios",
                        "deviceId": UUID().uuidString, //UIDevice.current.identifierForVendor?.uuidString ?? "",
                        "token": account.token ?? ""
                    ])
                    .build()
                if let client = self.client,
                   let msg = msg {
                    client.sendMessage(msg)
                }
            }
            break
        default:
            break
        }
    }
}
