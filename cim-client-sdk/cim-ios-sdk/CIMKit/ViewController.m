//
//  ViewController.m
//  CIMKit
//
//  Created by mason on 2020/11/7.
//

#import "ViewController.h"
#import "SocketRocket.h"

#import "GCDAsyncSocket.h"
#import "SentBody.pbobjc.h"
#import "Message.pbobjc.h"
#import "NSData+IM.h"
#import "NSString+IM.h"
#import "CIMHeader.h"
#import "AViewController.h"

@interface ViewController ()<GCDAsyncSocketDelegate,CIMPeerMessageObserver,CIMConnectionObserver>

@property (strong, nonatomic) SRWebSocket *imWebSocket;
@property (strong, nonatomic) GCDAsyncSocket * clientSocket;


@end

@implementation ViewController




- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    [self presentViewController:[AViewController new] animated:YES completion:nil];
}

- (void)viewDidLoad {
    [super viewDidLoad];

    self.view.backgroundColor = UIColor.redColor;

    [[CIMService instance] configHost:@"192.168.51.197" onPort:23456];

    [[CIMService instance] connectionBindUserId:@"111111"];

    [[CIMService instance] addMessageObserver:self];
    [[CIMService instance] addConnectionObserver:self];

    [[CIMService instance] removeMessageObserver:self];
    [[CIMService instance] removeConnectionObserver:self];


}


- (void)cimDidConnectClose{
    NSLog(@"cimDidConnectClose");

}

- (void)cimDidConnectError:(NSError *)error{
    NSLog(@"cimDidConnectError");

}

- (void)cimDidConnectSuccess{
    NSLog(@"cimDidConnectSuccess");

}

- (void)cimDidBindUserSuccess:(BOOL)bindSuccess{
    NSLog(@"cimDidBindUserSuccess");
}

- (void)cimHandleMessage:(CIMMessageModel *)msg{
    NSLog(@"ViewController:%@\nu用户：%@(%lld)\n---------",msg.content,msg.sender,msg.timestamp);
}
- (void)cimHandleMessageError:(NSData *)data{
    NSLog(@"cimHandleMessageError");
}








@end
