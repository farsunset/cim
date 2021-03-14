//
//  CIMSendMessage.h
//  CIMKit
//
//  Created by mason on 2020/11/13.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface CIMSendMessageData : NSObject

/// 心跳包数据
+(NSData *)initHeartbeatData;

/// 绑定用户数据
/// @param userId userId description
+(NSData *)initBindUserData:(NSString *)userId;

@end

NS_ASSUME_NONNULL_END
