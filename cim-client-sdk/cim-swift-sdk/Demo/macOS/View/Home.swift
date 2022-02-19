//
//  Home.swift
//  CIMApp (macOS)
//
//  Created by FeiYu on 2021/10/15.
//

import SwiftUI

var screen = NSScreen.main!.visibleFrame

struct Home: View {
    
    @StateObject var homeData = HomeViewModel()
    
    var body: some View {
        
        HStack(spacing:0) {
            VStack {
                
                TabButton(image: "message", title: "All Chats", selectedTab: $homeData.selectedTab)
                TabButton(image: "person", title: "Personal", selectedTab: $homeData.selectedTab)
                TabButton(image: "bubble.middle.bottom", title: "Bots", selectedTab: $homeData.selectedTab)
                TabButton(image: "slider.horizontal.3", title: "Edit", selectedTab: $homeData.selectedTab)
                Spacer()
                TabButton(image: "gear", title: "Settings", selectedTab: $homeData.selectedTab)
            }
            .padding()
            .padding(.top, 35)
            .background(BlurView())
            
            // Tab Content
            ZStack {
                
                switch homeData.selectedTab {
                case "All Chats": NavigationView{
                    AllChatsView()
                }
                case "Personal": Text("Personal")
                case "Bots": Text("Bots")
                case "Edit": Text("Edit")
                case "Settings": Text("Settings")
                default: Text("")
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
        .ignoresSafeArea(.all, edges: .all)
        .frame(minWidth: screen.width / 1.2, maxWidth: .infinity, minHeight: screen.height - 60, maxHeight: .infinity)
        .environmentObject(homeData)
    }
}

struct Home_Previews: PreviewProvider {
    static var previews: some View {
        Home()
    }
}
