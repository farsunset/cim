//
//  WebMessageDecoder.swift
//  cimsdk
//
//  Created by FeiYu on 2021/9/27.
//

import Foundation

class WebMessageDecoder {
    
    func decoder(_ data: Data) -> Transportable? {
        let msg = MessageBuilder()
            .set(transportData: data)
            .build()
        return msg
    }
}
