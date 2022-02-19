//
//  MessageDBInterceptor.swift
//  cimsdk
//
//  Created by FeiYu on 2021/10/2.
//

import Foundation

class MessageDatabaseInterceptor: MessageInterceptor {
    
    var uniqueID: String = UUID().uuidString
    
    let dbManager: CIMDBManager? = {
        
        let fileManager = FileManager.default
        if let documentsURL = fileManager.urls(for: .documentDirectory, in: .userDomainMask).first {
            let databasePath = documentsURL.appendingPathComponent("db.sqlite3").path
            print("directory path:", documentsURL.path)
            print("database path:", databasePath)
            if !fileManager.fileExists(atPath: databasePath) {
                fileManager.createFile(atPath: databasePath, contents: nil, attributes: nil)
            }
            let dbManager = CIMDBManager(path: databasePath)
            return dbManager
        }
        return nil
    }()
    
    /// MARK: - MessageListener
    func receiveMessage(_ message: Transportable) {
        do {
            try insertMsg(message)
        } catch {
            
        }
    }
    
    /// MARK:  - MessageSendListener
    func sendMessage(_ message: Transportable) {
        do {
            try insertMsg(message)
        } catch {
            
        }
    }
    
    func insertMsg(_ message: Transportable) throws {
        switch message.type {
        case .message(let msg):
            try dbManager?.messageDB.insert(msg)
            break
        default:
            break
        }
    }
    
}
