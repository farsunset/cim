//
//  WebMessageEncoder.swift
//  cimsdk
//
//  Created by FeiYu on 2021/9/27.
//

import Foundation

class WebMessageEncoder {
    
    func encoder(_ msg: Transportable) -> Data {
        var data = Data(msg.type.bytes)
        data.append(contentsOf: msg.body)
        return data
    }
}
