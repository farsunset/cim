//
//  RecentCardView.swift
//  CIMApp (macOS)
//
//  Created by FeiYu on 2021/10/15.
//

import SwiftUI

struct RecentCardView: View {
    var session: Session
    var body: some View {
        HStack {
            
            Image(session.userImage)
                .resizable()
                .aspectRatio(contentMode: .fill)
                .frame(width: 40, height: 40)
                .clipShape(Circle())
            
            VStack(spacing: 4) {
                HStack{
                    VStack(alignment: .leading, spacing: 4) {
                        Text(session.userName)
                            .fontWeight(.bold)
                        Text(session.lastMsg)
                            .font(.caption)
                    }
                    Spacer(minLength: 10)
                    VStack{
                        Text(session.lastMsgTime)
                            .font(.caption)
                        Text(session.pendingMsgs)
                            .font(.caption2)
                            .padding(5)
                            .foregroundColor(.white)
                            .background(Color.blue)
                            .clipShape(Circle())
                    }
                }
            }
        }
    }
}

struct RecentCardView_Previews: PreviewProvider {
    static var previews: some View {
        Home()
    }
}
