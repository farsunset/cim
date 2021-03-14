//
//  CIMMessageObserver.h
//  CIMKit
//
//  Created by mason on 2020/11/18.
//

#import <Foundation/Foundation.h>
#import "GCDAsyncSocket.h"
#import "CIMMessageModel.h"

@class CIMService;
/// 消息回调
@protocol CIMPeerMessageObserver <NSObject>

/// 接受到消息
/// @param msg msg description
-(void)cimHandleMessage:(CIMMessageModel * _Nonnull)msg;

/// 消息解析失败
/// @param data data description
-(void)cimHandleMessageError:(NSData * _Nonnull)data;


@end

/// 服务器连接回调
@protocol CIMConnectionObserver <NSObject>
@optional

/// 用户绑定成功
/// @param bindSuccess bindSuccess description
-(void)cimDidBindUserSuccess:(BOOL)bindSuccess;

/// 连接成功
-(void)cimDidConnectSuccess;

/// 断开连接
-(void)cimDidConnectClose;

/// 连接失败
/// @param error res description
-(void)cimDidConnectError:(NSError *_Nullable)error;

@end


NS_ASSUME_NONNULL_BEGIN

@interface CIMService : NSObject

+(CIMService*)instance;

/// 配置IM服务器
/// @param host host description
/// @param port port description
-(void)configHost:(NSString *)host onPort:(NSInteger)port;

/// 连接服务器并绑定用户
/// @param userId userId description
-(void)connectionBindUserId:(NSString *)userId;

/// 添加消息监听回调
/// @param observer observer description (可添加多个)不同时记得Remove
-(void)addMessageObserver:(id<CIMPeerMessageObserver>)observer;

/// 添加连接状态监听回调
/// @param observer observer description (可添加多个)不同时记得Remove
-(void)addConnectionObserver:(id<CIMConnectionObserver>)observer;

/// 移除监听
/// @param observer observer description
-(void)removeMessageObserver:(id<CIMPeerMessageObserver>)observer;

/// 移除监听回调
/// @param observer observer description
-(void)removeConnectionObserver:(id<CIMConnectionObserver>)observer;

/// 退出后台 断开连接
-(void)enterBackground;

/// 进入前台重新连接
-(void)enterForeground;

/// 重新连接
-(void)reconnect;

/// 断开连接
-(void)disconnect;











@end

NS_ASSUME_NONNULL_END
