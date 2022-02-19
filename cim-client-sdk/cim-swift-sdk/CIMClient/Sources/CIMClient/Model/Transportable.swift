//
//  Transportable.swift
//  cimsdk
//
//  Created by FeiYu on 2021/9/27.
//

import Foundation

public protocol Transportable {
    var body: [UInt8] { get }
    var type: MessageType { get }
}
