//
//  WSClient.swift
//  cimsdk
//
//  Created by FeiYu on 2021/10/1.
//

import Foundation
import Starscream

class WebsocketClient: WebSocketDelegate {
    
    var isConnected = false
    var socket: WebSocket!
    
    var account: Account?
    
    let webMessageEncoder = WebMessageEncoder()
    let webMessageDecoder = WebMessageDecoder()
    
    fileprivate var messageInterceptors: [MessageInterceptor] = []
    fileprivate var messageListeners: [MessageListener] = []
    fileprivate var connectListeners: [ConnectListener] = []
    
    convenience init(url: URL) {
        var request = URLRequest(url: url)
        request.timeoutInterval = 5 // Sets the timeout for the connection
        request.setValue("someother protocols", forHTTPHeaderField: "Sec-WebSocket-Protocol")
        request.setValue("14", forHTTPHeaderField: "Sec-WebSocket-Version")
        request.setValue("chat,superchat", forHTTPHeaderField: "Sec-WebSocket-Protocol")
        request.setValue(sdkVersion, forHTTPHeaderField: "SDKVersion")
        self.init()
        socket = WebSocket(request: request)
        socket.delegate = self
    }
    
    func appendMessageInterceptor(_ messageInterceptor: MessageInterceptor) {
        messageInterceptors.append(messageInterceptor)
    }
    
    func appendMessageListener(_ messageListener: MessageListener) {
        messageListeners.append(messageListener)
    }
    
    func appendConnectListener(_ connectListener: ConnectListener) {
        connectListeners.append(connectListener)
    }
    
    func removeMessageInterceptor(_ messageInterceptor: MessageInterceptor) {
        messageInterceptors.removeAll { $0.uniqueID == messageInterceptor.uniqueID }
    }
    
    func removeMessageListener(_ messageListener: MessageListener) {
        messageListeners.removeAll { $0.uniqueID == messageListener.uniqueID }
    }
    
    func removeConnectListener(_ connectListener: ConnectListener) {
        connectListeners.removeAll { $0.uniqueID == connectListener.uniqueID }
    }
    
    func connect(_ account: Account) {
        self.account = account
        messageInterceptors.forEach { $0.connect(.connecting) }
        connectListeners.forEach { $0.connect(.connecting) }
        socket.connect()
    }
    
    func disconnect() {
        messageInterceptors.forEach{ $0.connect(.disconnected("用户主动关闭Socket连接", 0))}
        connectListeners.forEach { $0.connect(.disconnected("用户主动关闭Socket连接", 0))}
        socket.disconnect()
    }
    
    func sendMessage(_ message: Transportable) {
        messageInterceptors.forEach { $0.sendMessage(message) }
        let data = webMessageEncoder.encoder(message)
        socket.write(data: data) { }
    }
    
    // MARK: - WebSocketDelegate
    func didReceive(event: WebSocketEvent, client: WebSocket) {
        switch event {
        case .connected(let headers):
            isConnected = true
            messageInterceptors.forEach{ $0.connect(.connected(headers)) }
            connectListeners.forEach { $0.connect(.connected(headers))}
        case .disconnected(let reason, let code):
            isConnected = false
            messageInterceptors.forEach{ $0.connect(.disconnected(reason, code)) }
            connectListeners.forEach { $0.connect(.disconnected(reason, code)) }
        case .text(let string):
            print("Received text: \(string)")
        case .binary(let data):
            if let transportMsg = webMessageDecoder.decoder(data) {
                messageInterceptors.forEach { $0.receiveMessage(transportMsg) }
                messageListeners.forEach { $0.receiveMessage(transportMsg) }
            } else {
                let error = CIMError(code: .msgDecodeErr)
                messageInterceptors.forEach { $0.receiveMessageWithError(error) }
                messageListeners.forEach { $0.receiveMessageWithError(error) }
            }
            break
        case .ping(_):
            break
        case .pong(_):
            break
        case .viabilityChanged(_):
            break
        case .reconnectSuggested(_):
            break
        case .cancelled:
            isConnected = false
            messageInterceptors.forEach { $0.connect(.cancelled) }
            connectListeners.forEach { $0.connect(.cancelled) }
        case .error(let error):
            isConnected = false
            messageInterceptors.forEach { $0.connect(.error(error)) }
            connectListeners.forEach { $0.connect(.error(error)) }
        }
    }
}

