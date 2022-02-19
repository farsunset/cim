//
//  CIMError.swift
//  cimsdk
//
//  Created by FeiYu on 2021/10/1.
//

import Foundation

public class CIMError: Error {
    
    public enum CIMErrorCode:UInt16 {
        
        case msgDecodeErr = 1000
        
        var desc: String {
            switch self {
            case .msgDecodeErr: return "消息解析失败"
            }
        }
    }

    let code: UInt16
    let reason: String
    
    public init(code: UInt16, reason: String?) {
        self.code = code
        self.reason = reason ?? "Unknown Error"
    }
    
    public init(code: CIMErrorCode) {
        self.code = code.rawValue
        self.reason = code.desc
    }
    
}
