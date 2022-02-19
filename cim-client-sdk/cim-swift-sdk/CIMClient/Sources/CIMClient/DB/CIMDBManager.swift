//
//  CIMDBManager.swift
//  cimsdk
//
//  Created by FeiYu on 2021/10/2.
//

import Foundation
import SQLite

class CIMDBManager {
    
    let path: String
    
    lazy var db: Connection? = {
        do {
            let db = try Connection(path)
            return db
        } catch {
            return nil
        }
    }()
    
    lazy var messageDB: MessageDB = { [unowned self] in
        let messageDB = MessageDB(db: self)
        return messageDB
    }()
    
    init(path: String) {
        self.path = path
    }
    
    func execute(query: String) throws {
        print("DB query: \(query)")
        if let db = db {
            do {
                try db.execute(query)
            } catch {
                print("DB 查询语句执行失败！")
            }
        } else {
            print("DB 连接失败！")
        }
    }
    
}
