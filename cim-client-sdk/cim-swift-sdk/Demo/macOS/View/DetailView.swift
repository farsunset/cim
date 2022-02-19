//
//  DetailView.swift
//  CIMApp (macOS)
//
//  Created by FeiYu on 2021/10/16.
//

import SwiftUI
import CIMClient

struct DetailView: View {
    @EnvironmentObject var homeData: HomeViewModel
    var session: Session
    var body: some View {
        HStack{
            VStack{
                HStack{
                    Text(session.userName)
                        .font(.title2)
                    Spacer()
                    Button(action: {}, label: {
                        Image(systemName: "magnifyingglass")
                            .font(.title2)
                    })
                        .buttonStyle(PlainButtonStyle())
                    
                    Button(action: {withAnimation{homeData.isExpanded.toggle()}}, label: {
                        Image(systemName: "sidebar.right")
                            .font(.title2)
                            .foregroundColor(homeData.isExpanded ? .blue : .primary)
                    })
                        .buttonStyle(PlainButtonStyle())
                }
                .padding()
                
                MessageView(session: session)
                
                HStack(spacing:15){
                    Button(action: {
                        print(homeData.message)
                    }, label: {
                        Image(systemName: "paperplane")
                            .font(.title2)
                    })
                        .buttonStyle(PlainButtonStyle())
                    
                    TextField("Enter Message", text: $homeData.message, onCommit: {
                        homeData.sendMessage(session: session)
                    })
                        .textFieldStyle(PlainTextFieldStyle())
                        .padding(.vertical,8)
                        .padding(.horizontal)
                        .clipShape(Capsule())
                        .background(Capsule().strokeBorder(Color.white))
                    
                    Button(action: { }, label: {
                        Image(systemName: "face.smiling.fill")
                            .font(.title2)
                    })
                        .buttonStyle(PlainButtonStyle())
                    
                    Button(action: {}, label: {
                        Image(systemName: "mic")
                            .font(.title2)
                    })
                        .buttonStyle(PlainButtonStyle())
                }
                .padding([.horizontal,.bottom])
            }
            
            ExpandedView(session: session)
                .background(BlurView())
                .frame(width: homeData.isExpanded ? nil : 0)
                .opacity(homeData.isExpanded ? 1 : 0)
        }
        .ignoresSafeArea(.all, edges: .all)
        .onAppear(perform: {
            homeData.clearUnReadMessage(session: session)
        })
    }
}

struct DetailView_Previews: PreviewProvider {
    static var previews: some View {
        Home()
    }
}

struct MessageView: View {
    
    @EnvironmentObject var homeData: HomeViewModel
    var session: Session
    var body: some View {
        
        GeometryReader {render in
            ScrollView{
                
                ScrollViewReader{proxy in
                    VStack{
                        ForEach(session.allMsgs){message in
                            
                            MessageCardView(message: message, session: session, width: render.frame(in: .global).width)
                                .padding(.bottom,20)
                                .tag(message.id)
                        }
                    }
                    .padding(.leading,10)
                    .onAppear(perform: {
                        let lastID = session.allMsgs.last!.id
                        proxy.scrollTo(lastID, anchor: .bottom)
                    })
                    .onChange(of: session.allMsgs, perform: { value in
                        withAnimation{
                            let lastID = value.last!.id
                            proxy.scrollTo(lastID, anchor: .bottom)
                        }
                    })
                }
            }
        }
        
    }
}

struct MessageCardView: View {
    
    @EnvironmentObject var homeData: HomeViewModel
    
    var message: Message
    var session: Session
    var width: CGFloat
    
    var body: some View {
        
        HStack(spacing: 10) {
            let myMessage = (message.sender == "\(homeData.account.id)")
            if myMessage {
                
                Spacer()
                
                Text(message.content)
                    .foregroundColor(.white)
                    .padding(10)
                    .background(Color.blue)
                    .clipShape(MessageBubble(myMessage: myMessage))
                    .frame(minWidth: 0, maxWidth: width / 2, alignment: .trailing)
                
                Image(session.userImage)
                    .resizable()
                    .aspectRatio(contentMode: .fill)
                    .frame(width: 35, height: 35)
                    .clipShape(Circle())
                    .offset(y: 20)
                
            } else {
                
                Image(session.userImage)
                    .resizable()
                    .aspectRatio(contentMode: .fill)
                    .frame(width: 35, height: 35)
                    .clipShape(Circle())
                    .offset(y: 20)
                
                Text(message.content)
                    .foregroundColor(.white)
                    .padding(10)
                    .background(Color.primary.opacity(0.2))
                    .clipShape(MessageBubble(myMessage: myMessage))
                    .frame(minWidth: 0, maxWidth: width / 2, alignment: .leading)
                
                Spacer()
            }
        }
    }
}


struct MessageBubble: Shape {
    
    var myMessage: Bool
    
    func path(in rect: CGRect) -> Path {
        return Path{ path in
            let pt1 = CGPoint(x: 0, y: 0)
            let pt2 = CGPoint(x: rect.width, y: 0)
            let pt3 = CGPoint(x: rect.width, y: rect.height)
            let pt4 = CGPoint(x: 0, y: rect.height)
            
            if myMessage {
                path.move(to: pt3)
                path.addArc(tangent1End: pt3, tangent2End: pt4, radius: 15)
                path.addArc(tangent1End: pt4, tangent2End: pt1, radius: 15)
                path.addArc(tangent1End: pt1, tangent2End: pt2, radius: 15)
                path.addArc(tangent1End: pt2, tangent2End: pt3, radius: 15)
            } else {
                path.move(to: pt4)
                path.addArc(tangent1End: pt4, tangent2End: pt1, radius: 15)
                path.addArc(tangent1End: pt1, tangent2End: pt2, radius: 15)
                path.addArc(tangent1End: pt2, tangent2End: pt3, radius: 15)
                path.addArc(tangent1End: pt3, tangent2End: pt4, radius: 15)
            }
        }
    }
}


struct ExpandedView: View {
    @EnvironmentObject var homeData: HomeViewModel
    var session: Session
    var body: some View {
        HStack(spacing: 0) {
            Divider()
            
            VStack(spacing: 25) {
                
                Image(session.userImage)
                    .resizable()
                    .aspectRatio(contentMode: .fill)
                    .frame(width: 90, height: 90)
                    .clipShape(Circle())
                    .padding(.top,35)
                
                Text(session.userName)
                    .font(.title)
                    .fontWeight(.bold)
                
                HStack{
                    
                    Button(action: {}, label: {
                        VStack{
                            Image(systemName: "bell.slash")
                                .font(.title2)
                            Text("Mute")
                        }
                        .contentShape(Rectangle())
                    })
                        .buttonStyle(PlainButtonStyle())
                    
                    Spacer()
                    
                    Button(action: {}, label: {
                        VStack{
                            Image(systemName: "hand.raised.fill")
                                .font(.title2)
                            Text("Block")
                        }
                        .contentShape(Rectangle())
                    })
                        .buttonStyle(PlainButtonStyle())
                    
                    Spacer()
                    
                    Button(action: {}, label: {
                        VStack{
                            Image(systemName: "exclamationmark.triangle")
                                .font(.title2)
                            Text("Report")
                        }
                        .contentShape(Rectangle())
                    })
                        .buttonStyle(PlainButtonStyle())
                }
                .foregroundColor(.gray)
                
                Picker(selection: $homeData.pickedTab, label: Text("Picker"), content: {
                    Text("Media").tag("Media")
                    Text("Links").tag("Links")
                    Text("Audio").tag("Audio")
                    Text("Files").tag("Files")
                })
                    .labelsHidden()
                    .padding(.vertical)
                    .pickerStyle(SegmentedPickerStyle())
                
                ScrollView{
                    if homeData.pickedTab == "Media" {
                        
                        LazyVGrid(columns: Array(repeating: GridItem(.flexible(), spacing: 10), count: 3),  spacing: 10,  content: {
                            
                            ForEach(1...8, id: \.self) {
                                index in
                                Image("avatar")
                                    .resizable()
                                    .aspectRatio(contentMode: .fill)
                                    .frame(width: 80, height: 80)
                                    .cornerRadius(3)
                            }
                        })
                    } else {
                        Text("No \(homeData.pickedTab)")
                    }
                }
                
            }
            .padding(.horizontal)
            .frame(maxWidth: 300)
        }
    }
}
