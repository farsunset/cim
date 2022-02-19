//
//  HomeViewModel.swift
//  CIMApp (macOS)
//
//  Created by FeiYu on 2021/10/15.
//

import SwiftUI
import CIMClient

class HomeViewModel: ObservableObject, MessageInterceptor {
    
    @Published var selectedTab = "All Chats"
    
    @Published var sessions : [Session] = localSessions
    
    @Published var selectedSession : String? = localSessions.first?.id
    
    @Published var search = ""
    
    @Published var message = ""
    
    @Published var isExpanded = false
    
    @Published var pickedTab = "Media"
    
    let uniqueID: String = UUID().uuidString
    let account = Account(id: 123456, name: "FeiYu", token: "qwqewewrettryry")
    let imClient = CIMClient(url: "ws://192.168.2.100:34567")
    
    func sendMessage(session: Session) {
        
        if message != "" {
            imClient.msgSender.sendTextMessage(message, receiver: session.id)
            message = ""
        }
    }
    
    func clearUnReadMessage(session: Session) {
        let index = sessions.firstIndex { (s) -> Bool in
            return s.id == session.id
        } ?? -1
        if index != -1 {
            var session = sessions[index]
            session.pendingMsgs = "0"
            sessions[index] = session
        }
    }
    
    init() {
        imClient.appendMessageInterceptor(self)
        imClient.connect(account)
    }
    
    /// MARK: - MessageInterceptor
    func connect(_ event: ConnectEvent) {
        
    }
    
    func sendMessage(_ message: Transportable) {
        switch message.type {
        case .message(let msg):
            let index = sessions.firstIndex { (session) -> Bool in
                return session.id == msg.receiver
            } ?? -1
            
            if index != -1 {
                var session = sessions[index]
                session.allMsgs.append(msg)
                session.lastMsg = msg.content
                session.lastMsgTime = msg.timestamp.timeStampToTime()
                session.pendingMsgs = "0"
                sessions[index] = session
            } else {
                let session = Session(id: msg.receiver, lastMsg: msg.content, lastMsgTime: msg.timestamp.timeStampToTime(), pendingMsgs: "0", userName: "uid:\(msg.receiver)", userImage: "avatar", allMsgs: [msg])
                sessions.append(session)
            }
            break
        default:
            break
        }
    }
    
    func receiveMessage(_ message: Transportable) {
        switch message.type {
        case .message(let msg):
            let index = sessions.firstIndex { (session) -> Bool in
                return session.id == msg.sender
            } ?? -1
            
            if index != -1 {
                var session = sessions[index]
                session.lastMsg = msg.content
                session.lastMsgTime = msg.timestamp.timeStampToTime()
                session.allMsgs.append(msg)
                if var pendingMsgs = Int(session.pendingMsgs) {
                    pendingMsgs += 1
                    session.pendingMsgs = "\(pendingMsgs)"
                }
                if let selectedSession = selectedSession {
                    if session.id == selectedSession {
                        session.pendingMsgs = "0"
                    }
                }
                sessions[index] = session
            } else {
                let session = Session(id: msg.sender, lastMsg: msg.content, lastMsgTime: msg.timestamp.timeStampToTime(), pendingMsgs: "1", userName: "uid:\(msg.sender)", userImage: "avatar", allMsgs: [msg])
                sessions.append(session)
            }
            break
        default:
            break
        }
    }
    
    func receiveMessageWithError(_ error: Error) {
        
    }
}
