//
//  HeartInterceptor.swift
//  cimsdk
//
//  Created by FeiYu on 2021/10/1.
//

import Foundation

class HeartInterceptor: MessageInterceptor {

    var uniqueID: String = UUID().uuidString
    
    var lastHartTime: Int64 = Date().currentTimestamp()
    
    weak var client: CIMClient?
    
    var timer: Timer?
    
    init(client: CIMClient) {
        self.client = client
    }
    
    func startHeartCheck() {
        if #available(iOS 10.0, *) {
            if (self.timer == nil) {
                self.timer = Timer.scheduledTimer(withTimeInterval: TimeInterval(3), repeats: true, block: { [weak self] timer in
                    let currentTime = Date().currentTimestamp()
                    if let self = self {
                        if ((currentTime - self.lastHartTime) > 30*1000) {
                            self.client?.reConnect()
                        }
                    }
                })
            }
        }
    }
    
    func stopHeartCheck() {
        if (self.timer != nil) {
            self.timer?.invalidate()
            self.timer = nil
        }
    }
    
    func connect(_ event: ConnectEvent) {
        switch event {
        case .disconnected(_, _):
            stopHeartCheck()
            break
        case .error(_):
            self.client?.reConnect()
            break
        case .cancelled:
            stopHeartCheck()
            break
        case .bindUser(let success):
            if (success) {
                startHeartCheck()
            }
            break
        default:
            break
        }
    }
    
    /// MARK: - MessageListener
    func receiveMessage(_ message: Transportable) {
        lastHartTime = Date().currentTimestamp()
        switch message.type {
        case .ping(_):
            let pong = Pong()
            self.client?.sendMessage(pong)
            break
        default:
            break
        }
    }
}
