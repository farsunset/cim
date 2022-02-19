//
//  MessageInterceptor.swift
//  cimsdk
//
//  Created by FeiYu on 2021/10/1.
//

import Foundation

public protocol MessageInterceptor: ConnectListener & MessageSendListener & MessageListener {
    var uniqueID: String { get }
}
