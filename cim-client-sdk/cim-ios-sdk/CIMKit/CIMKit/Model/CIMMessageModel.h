//
//  CIMMessageModel.h
//  CIMKit
//
//  Created by mason on 2020/11/19.
//

#import <Foundation/Foundation.h>
#import "Message.pbobjc.h"
NS_ASSUME_NONNULL_BEGIN

@interface CIMMessageModel : NSObject

@property(nonatomic, readwrite) int64_t id_p;

@property(nonatomic, readwrite, copy) NSString *action;

@property(nonatomic, readwrite, copy) NSString *content;

@property(nonatomic, readwrite, copy) NSString *sender;

@property(nonatomic, readwrite, copy) NSString *receiver;

@property(nonatomic, readwrite, copy) NSString *extra;

@property(nonatomic, readwrite, copy) NSString *title;

@property(nonatomic, readwrite, copy) NSString *format;

@property(nonatomic, readwrite) int64_t timestamp;

+(CIMMessageModel *)initWithProtoMdoel:(MessageModel *)model;

@end

NS_ASSUME_NONNULL_END
