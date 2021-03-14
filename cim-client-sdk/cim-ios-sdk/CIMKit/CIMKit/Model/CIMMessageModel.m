//
//  CIMMessageModel.m
//  CIMKit
//
//  Created by mason on 2020/11/19.
//

#import "CIMMessageModel.h"

@implementation CIMMessageModel


+(CIMMessageModel *)initWithProtoMdoel:(MessageModel *)model{

    CIMMessageModel * cimMessageModel = [CIMMessageModel new];
    cimMessageModel.id_p = model.id_p;
    cimMessageModel.title = model.title;
    cimMessageModel.action = model.action;
    cimMessageModel.timestamp = model.timestamp;
    cimMessageModel.extra = model.extra;
    cimMessageModel.format = model.format;
    cimMessageModel.sender = model.sender;
    cimMessageModel.content = model.content;
    cimMessageModel.receiver = model.receiver;
    return cimMessageModel;
}

@end
