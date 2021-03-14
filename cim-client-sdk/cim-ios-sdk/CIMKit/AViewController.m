//
//  AViewController.m
//  CIMKit
//
//  Created by mason on 2020/11/19.
//

#import "AViewController.h"
#import "CIMService.h"

@interface AViewController ()<CIMPeerMessageObserver>

@end

@implementation AViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.

    self.view.backgroundColor = UIColor.whiteColor;

    [[CIMService instance] addMessageObserver:self];
}


- (void)cimhandleMessage:(CIMMessageModel *)msg{
    NSLog(@"AViewController:%@",msg.content);
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
