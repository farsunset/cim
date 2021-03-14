//
//  NSString+IM.h
//  CIMKit
//
//  Created by mason on 2020/11/13.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface NSString (IM)


-(NSData*) convertBytesStringToData ;

- (NSString *)hexToDecimal ;

//data -> 16进制
+(NSString *)hexStringFormData:(NSData *)data;

@end

NS_ASSUME_NONNULL_END
