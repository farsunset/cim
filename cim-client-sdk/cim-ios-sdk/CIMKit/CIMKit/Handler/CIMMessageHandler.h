//
//  CIMMessageDeCode.h
//  CIMKit
//
//  Created by mason on 2020/11/13.
//

#import <Foundation/Foundation.h>
#import "CIMService.h"
#import "GCDAsyncSocket.h"
#import "SentBody.pbobjc.h"
#import "Message.pbobjc.h"
#import "NSData+IM.h"
#import "NSString+IM.h"
#import "CIMSendMessageData.h"
#import "ReplyBody.pbobjc.h"

NS_ASSUME_NONNULL_BEGIN

typedef enum CIMMessageType: NSUInteger {
    CIMMessageTypeC_H_RS = 0,//客户端心跳响应
    CIMMessageTypeS_H_RQ,//服务端心跳请求
    CIMMessageTypeMESSAGE,//服务端推送的消息
    CIMMessageTypeSENT_BODY,//客户端发送的sentBody请求
    CIMMessageTypeREPLY_BODY//sentBody请求的异步响应replyBody
} CIMMessageType;


@interface CIMMessageHandler : NSObject

/// 处理服务数据
/// @param data data description
-(void)doCode:(nullable NSData *)data socket:(GCDAsyncSocket *)sock;

-(void)addPeerMessageObservers:(id<CIMPeerMessageObserver>)objects;
-(void)addConnectionObservers:(id<CIMConnectionObserver>)objects;
-(void)removePeerMessageObservers:(id<CIMPeerMessageObserver>)objects;
-(void)removeConnectionObservers:(id<CIMConnectionObserver>)objects;

-(void)handlerMessage:(CIMMessageModel *)message;
-(void)handlerMessageError:(NSData *)data;
-(void)handlerBindUser:(BOOL)bindSuccess;

-(void)handlerConnectSuccess;
-(void)handlerConnectClose;
-(void)handlerConnectError:(NSError *)error;

@end

NS_ASSUME_NONNULL_END
