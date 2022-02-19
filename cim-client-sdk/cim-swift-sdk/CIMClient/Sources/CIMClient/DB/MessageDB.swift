//
//  MessageDB.swift
//  cimsdk
//
//  Created by FeiYu on 2021/10/2.
//

import Foundation

class MessageDB {
    
    weak var db: CIMDBManager?
    
    init(db: CIMDBManager) {
        self.db = db
        do {
            try createTable()
        } catch {
            
        }
    }
    
    func createTable() throws {
        
        let createSQL = """
                        CREATE TABLE IF NOT EXISTS "messages" (
                            "id"    INT64 NOT NULL,
                            "action"    TEXT NOT NULL,
                            "title"    TEXT NOT NULL,
                            "content"    TEXT NOT NULL,
                            "sender"    TEXT NOT NULL,
                            "receiver"    TEXT NOT NULL,
                            "format"    TEXT NOT NULL,
                            "extra"    TEXT NOT NULL,
                            "timestamp"    INT64 NOT NULL,
                            PRIMARY KEY("id")
                        );
                        """
        try db?.execute(query: createSQL)
    }
    
    func insert(_ message: Message) throws {
        
        let insertSQL = """
                        REPLACE INTO "messages" ("id","action","title","content","sender","receiver","format","extra","timestamp") VALUES (
                        \(message.id),
                        "\(message.action)",
                        "\(message.title)",
                        "\(message.content)",
                        "\(message.sender)",
                        "\(message.receiver)",
                        "\(message.format)",
                        "\(message.extra)",
                        \(message.timestamp));
                        """
        try db?.execute(query: insertSQL)
    }
}
