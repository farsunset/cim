//
//  Account.swift
//  cimsdk
//
//  Created by FeiYu on 2021/10/1.
//

import Foundation

public struct Account: Codable {
    public let id: Int64
    public let name: String
    public let token: String?
    
    public init(id: Int64, name: String, token: String?) {
        self.id = id
        self.name = name
        self.token = token
    }
}
