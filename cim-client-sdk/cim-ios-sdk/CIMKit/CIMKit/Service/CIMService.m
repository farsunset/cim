//
//  CIMMessageObserver.m
//  CIMKit
//
//  Created by mason on 2020/11/18.
//

#import "CIMService.h"
#import "CIMSendMessageData.h"
#import "CIMMessageHandler.h"


@interface CIMService()<GCDAsyncSocketDelegate>

@property (assign, nonatomic)BOOL deBugLog;

@property(nonatomic,copy,readonly)NSString * host;
@property(nonatomic,assign,readonly)NSInteger  port;

@property (strong, nonatomic) GCDAsyncSocket * clientSocket;
@property (strong, nonatomic)dispatch_queue_t queue;

@property (strong, nonatomic)CIMMessageHandler *handler;

@property (copy, nonatomic)NSString *userId;



@end

@implementation CIMService

+(CIMService*)instance {
    static CIMService *imService;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        if (!imService) {
            imService = [[CIMService alloc] init];
        }
    });
    return imService;
}

-(id)init {
    self = [super init];
    if (self) {

    }
    return self;
}

-(void)configHost:(NSString *)host onPort:(NSInteger)port{
    _host = host;
    _port = port;
}


- (void)connectionBindUserId:(NSString *)userId{
    if(userId.length == 0){
        [self.handler handlerBindUser:NO];
        return;
    }
    self.userId = userId;
    NSAssert(self.host.length > 1, @"定调用configHost 配置host和port");
    NSAssert(self.port > 1, @"定调用configHost 配置host和port");
    //接服务器
    NSError *error;
    [self.clientSocket connectToHost:self.host onPort:self.port error:&error];
    if (error) {
        [self.handler handlerConnectError:error];
    }else{
        NSLog(@"socket 成功");
        [self.clientSocket writeData:[CIMSendMessageData initBindUserData:userId] withTimeout:-1 tag:0];
    }
}






#pragma mark Observe
- (void)addMessageObserver:(id<CIMPeerMessageObserver>)observer{
    [self.handler addPeerMessageObservers:observer];
}

- (void)addConnectionObserver:(id<CIMConnectionObserver>)observer{
    [self.handler addConnectionObservers:observer];
}

- (void)removeMessageObserver:(id<CIMPeerMessageObserver>)observer{
    [self.handler removePeerMessageObservers:observer];
}

- (void)removeConnectionObserver:(id<CIMConnectionObserver>)observer{
    [self.handler removeConnectionObservers:observer];
}
#pragma mark -Connection methods
-(void)enterBackground{
    if(self.clientSocket.isConnected){
        [self.clientSocket disconnect];
    }
}
-(void)enterForeground{
    if(!self.clientSocket.isConnected){
        [self connectionBindUserId:self.userId];
    }
}

-(void)reconnect{
    if(!self.clientSocket.isConnected){
        [self connectionBindUserId:self.userId];
    }
}

-(void)disconnect{
    if(self.clientSocket.isConnected){
        [self.clientSocket disconnect];
        self.clientSocket = nil;
    }
}

#pragma mark - GCDAsyncSocketDelegate
// 连接成功
- (void)socket:(GCDAsyncSocket *)sock didConnectToHost:(NSString *)host port:(uint16_t)port {
    NSLog(@"socket 连接成功");
    [self.clientSocket readDataWithTimeout:-1 tag:0];
    [self.handler handlerConnectSuccess];
}

// 已经断开链接
- (void)socketDidDisconnect:(GCDAsyncSocket *)sock withError:(NSError *)err {
    NSLog(@"socket 断开连接%@",err.localizedDescription);
    [self.handler handlerConnectClose];
}

//读取到数据
- (void)socket:(GCDAsyncSocket *)sock didReadData:(NSData *)data withTag:(long)tag {
    [self.clientSocket readDataWithTimeout:-1 tag:0];
    //数据处理
    NSLog(@"didReadData");
    [self.handler doCode:data socket:sock];
}


#pragma  mark lazy
- (GCDAsyncSocket *)clientSocket{
    if(!_clientSocket){
        self.queue =  dispatch_queue_create("com.cim.IMQueue", DISPATCH_QUEUE_CONCURRENT);
        _clientSocket = [[GCDAsyncSocket alloc] initWithDelegate:self delegateQueue:self.queue];
    }
    return _clientSocket;
}

- (CIMMessageHandler *)handler{
    if(!_handler){
        _handler = [[CIMMessageHandler alloc] init];
    }
    return _handler;
}



@end
