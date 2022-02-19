//
//  Session.swift
//  CIMApp (macOS)
//
//  Created by FeiYu on 2021/10/15.
//

import SwiftUI
import CIMClient

struct Session: Identifiable {
    var id: String
    var lastMsg: String
    var lastMsgTime: String
    var pendingMsgs: String
    var userName: String
    var userImage: String
    var allMsgs: [Message]
}

var localSessions : [Session] = []
