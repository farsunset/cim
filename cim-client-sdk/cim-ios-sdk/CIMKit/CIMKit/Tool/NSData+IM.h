//
//  NSData+IM.h
//  CIMKit
//
//  Created by mason on 2020/11/13.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface NSData (IM)

- (NSString *)convertDataToHexStr;

+ (NSString *)convertDataToHexStr:(NSData *)data;

@end

NS_ASSUME_NONNULL_END
