//
//  ContentView.swift
//  Shared
//
//  Created by FeiYu on 2021/10/10.
//

import SwiftUI

struct ContentView: View {
    var body: some View {
        Button("sent msg") {
            imClient.msgSender.sendTextMessage("hello", receiver: "123456")
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
