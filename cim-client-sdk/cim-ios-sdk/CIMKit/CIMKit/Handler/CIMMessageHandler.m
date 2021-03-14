//
//  CIMMessageDeCode.m
//  CIMKit
//
//  Created by Siter By:750692607@qq.com on 2020/11/13.
//

#import "CIMMessageHandler.h"


@interface  CIMMessageHandler()

@property(strong,nonatomic)NSData * data;

@property(strong,nonatomic)NSMutableArray * connectionObservers;
@property(strong,nonatomic)NSMutableArray * messageObservers;

@property(copy,nonatomic)NSString *mainQueueLabel;


@end

@implementation CIMMessageHandler


- (instancetype)init
{
    self = [super init];
    if (self) {
        const char *label = dispatch_queue_get_label(dispatch_get_main_queue());
        self.mainQueueLabel = label ? [[NSString alloc] initWithUTF8String:label] : @"";
        self.connectionObservers = [NSMutableArray array];
        self.messageObservers = [NSMutableArray array];
    }
    return self;
}

/// 处理服务数据
/// @param data data description
-(void)doCode:(nullable NSData *)data socket:(GCDAsyncSocket *)sock{
    self.data = data;
    //TLV格式
    int allDateLenght = [self getMessageVauleLenght] + 3;
    do {
        [sock readDataWithTimeout:-1 tag:0];
    } while (self.data.length != allDateLenght);

    NSData *headTagData = [data subdataWithRange:NSMakeRange(0, 1)];//取得头部数据
    CIMMessageType messageType = (CIMMessageType)[headTagData convertDataToHexStr].integerValue;

    //心跳包请求
    if(messageType == CIMMessageTypeS_H_RQ){
        [sock writeData:[CIMSendMessageData initHeartbeatData] withTimeout:-1 tag:0];
        return;
    }
    //绑定异步回调
    if(messageType == CIMMessageTypeREPLY_BODY){

        NSData * messageData = [data subdataWithRange:NSMakeRange(3, [self getMessageVauleLenght])];

        NSError * error;

        ReplyBodyModel * replyBodyModel = [[ReplyBodyModel alloc] initWithData:messageData error:&error];
        if(!error){
            [self handlerBindUser:[replyBodyModel.code isEqualToString:@"200"]];
        }else{
            [self handlerBindUser:NO];
        }

        return;
    }
    //服务端消息
    if(messageType == CIMMessageTypeMESSAGE){

        NSData * messageData = [data subdataWithRange:NSMakeRange(3, [self getMessageVauleLenght])];

        NSError * error;

        MessageModel * messgae = [[MessageModel alloc] initWithData:messageData error:&error];

        if(!error){
            //返回消息
            [self handlerMessage:[CIMMessageModel initWithProtoMdoel:messgae]];
        }else{
            [self handlerMessageError:data];
        }
        return;
    }

}


#pragma mark Observe methods
-(void)handlerConnectSuccess{
    [self runOnMainThread:^{
        for (NSValue *value in self.messageObservers) {
            id<CIMConnectionObserver> ob = [value nonretainedObjectValue];
            if ([ob respondsToSelector:@selector(cimDidConnectSuccess)]) {
                [ob cimDidConnectSuccess];
            }
        }
    }];
}



-(void)handlerConnectError:(NSError *)error{
    [self runOnMainThread:^{
        for (NSValue *value in self.messageObservers) {
            id<CIMConnectionObserver> ob = [value nonretainedObjectValue];
            if ([ob respondsToSelector:@selector(cimDidConnectError:)]) {
                [ob cimDidConnectError:error];
            }
        }
    }];
}

-(void)handlerMessage:(CIMMessageModel *)message{
    [self runOnMainThread:^{
        for (NSValue *value in self.messageObservers) {
            id<CIMPeerMessageObserver> ob = [value nonretainedObjectValue];
            if ([ob respondsToSelector:@selector(cimHandleMessage:)]) {
                [ob cimHandleMessage:message];
            }
        }
    }];
}

-(void)handlerMessageError:(NSData *)data{
    [self runOnMainThread:^{
        for (NSValue *value in self.messageObservers) {
            id<CIMPeerMessageObserver> ob = [value nonretainedObjectValue];
            if ([ob respondsToSelector:@selector(cimHandleMessageError:)]) {
                [ob cimHandleMessageError:data];
            }
        }
    }];
}
-(void)handlerConnectClose{
    [self runOnMainThread:^{
        for (NSValue *value in self.messageObservers) {
            id<CIMConnectionObserver> ob = [value nonretainedObjectValue];
            if ([ob respondsToSelector:@selector(cimDidConnectClose)]) {
                [ob cimDidConnectClose];
            }
        }
    }];
}

-(void)handlerBindUser:(BOOL)bindSuccess{
    [self runOnMainThread:^{
        for (NSValue *value in self.connectionObservers) {
            id<CIMConnectionObserver> ob = [value nonretainedObjectValue];
            if ([ob respondsToSelector:@selector(cimDidBindUserSuccess:)]) {
                [ob cimDidBindUserSuccess:bindSuccess];
            }
        }
    }];
}

-(void)addPeerMessageObservers:(id<CIMPeerMessageObserver>)objects{
    NSValue *value = [NSValue valueWithNonretainedObject:objects];
    if (![self.messageObservers containsObject:value]) {
        [self.messageObservers addObject:value];
    }
}

-(void)addConnectionObservers:(id<CIMConnectionObserver>)objects{
    NSValue *value = [NSValue valueWithNonretainedObject:objects];
    if (![self.connectionObservers containsObject:value]) {
        [self.connectionObservers addObject:value];
    }
}

-(void)removePeerMessageObservers:(id<CIMPeerMessageObserver>)objects{
    NSValue *value = [NSValue valueWithNonretainedObject:objects];
    if (![self.messageObservers containsObject:value]) {
        [self.messageObservers removeObject:value];
    }
}

-(void)removeConnectionObservers:(id<CIMConnectionObserver>)objects{
    NSValue *value = [NSValue valueWithNonretainedObject:objects];
    if ([self.connectionObservers containsObject:value]) {
        [self.connectionObservers removeObject:value];
    }
}

#pragma mark private methods
-(void)runOnQueue:(NSString*)queueLabel block:(dispatch_block_t)block {
    const char *s = dispatch_queue_get_label(DISPATCH_CURRENT_QUEUE_LABEL);
    NSString *label = s ? [[NSString alloc] initWithUTF8String:s] : @"";

    if ([queueLabel isEqualToString:label]) {
        block();
    } else {
        dispatch_async(dispatch_get_main_queue(), ^{
            block();
        });
    }
}
-(void)runOnMainThread:(dispatch_block_t)block {
    [self runOnQueue:self.mainQueueLabel block:block];
}


/// 获取data 消息长度
-(int)getMessageVauleLenght{

    NSData * lv = [self.data subdataWithRange:NSMakeRange(1, 1)];
    NSData * hv = [self.data subdataWithRange:NSMakeRange(2, 1)];

    int lvString  = [[lv convertDataToHexStr] hexToDecimal].intValue;
    int hvString  = [[hv convertDataToHexStr] hexToDecimal].intValue;

    return lvString | hvString << 8;
}


- (NSData *)data{
    if(!_data){
        _data = [[NSData alloc] init];
    }
    return _data;
}


@end
