//
//  AllChatsView.swift
//  CIMApp (macOS)
//
//  Created by FeiYu on 2021/10/15.
//

import SwiftUI

struct AllChatsView: View {
    
    @EnvironmentObject var homeData: HomeViewModel
    var body: some View {
        // Side Tab View....
        
        VStack {
            
            HStack {
                Spacer()
                Button(action: {}, label: {
                    Image(systemName: "plus")
                        .font(.title2)
                        .foregroundColor(.white)
                })
                    .buttonStyle(PlainButtonStyle())
            }
            .padding(.horizontal)
            
            HStack{
                Image(systemName: "magnifyingglass")
                    .foregroundColor(.gray)
                TextField("Search", text: $homeData.search)
                    .textFieldStyle(PlainTextFieldStyle())
            }
            .padding(.vertical, 8)
            .padding(.horizontal)
            .background(Color.primary.opacity(0.15))
            .cornerRadius(10)
            .padding()
            
            
            List(selection: $homeData.selectedSession){
                
                ForEach(homeData.sessions) { session in
                    
                    NavigationLink(destination: DetailView(session: session)) {
                        RecentCardView(session: session)
                    }
                    
                }
            }
        }
        .frame(minWidth: 280)
        .listStyle(SidebarListStyle())
        
        
    }
}

struct AllChatsView_Previews: PreviewProvider {
    static var previews: some View {
        Home()
    }
}
