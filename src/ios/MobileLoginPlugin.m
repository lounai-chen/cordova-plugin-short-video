/********* ShortVideoPlugin.m Cordova Plugin Implementation *******/
 
#import <Cordova/CDV.h>
  
#import <AVKit/AVKit.h>
#import <Foundation/Foundation.h>
#import <CommonCrypto/CommonCrypto.h>
 

#import <ATAuthSDK/ATAuthSDK.h>
#import <PNSNetDetect/PNSNetDetect.h>
#import <YTXMonitor/YTXMonitor.h>
#import <YTXOperators/YTXOperators.h>
 

@interface ShortVideoPlugin : CDVPlugin {
    NSString *webUrlString;
} 
 
- (void)onekey_init:(CDVInvokedUrlCommand*)command;
- (void)onekey_login:(CDVInvokedUrlCommand*)command;

@end

@implementation ShortVideoPlugin

static NSString* myAsyncCallBackId = nil;
static CDVPluginResult *pluginResult = nil;
static ShortVideoPlugin *selfplugin = nil;

- (void)pluginInitialize {
    CDVViewController *viewController = (CDVViewController *)self.viewController;
   
}

- (void)onekey_init:(CDVInvokedUrlCommand *)command
{
    selfplugin = self;
    myAsyncCallBackId = command.callbackId;
 
    
    pluginResult = [CDVPluginResult resultWithStatus: CDVCommandStatus_NO_RESULT];
    [pluginResult setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:pluginResult callbackId: command.callbackId];
}


- (void)onekey_login:(CDVInvokedUrlCommand *)command
{
    selfplugin = self;
    myAsyncCallBackId = command.callbackId;
    
  
}
 
 



-  (void)  sendCmd : (NSString *)msg
{
    if(myAsyncCallBackId != nil)
    {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString: msg ];
        //将 CDVPluginResult.keepCallback 设置为 true ,则不会销毁callback
        [pluginResult  setKeepCallbackAsBool:YES];
        [selfplugin.commandDelegate sendPluginResult:pluginResult callbackId: myAsyncCallBackId];

    }
}
 

-(void)msgButtonClick{
    [[TXCommonHandler sharedInstance] cancelLoginVCAnimated:YES complete:nil];
    [self sendCmd:  @"1|"];
}
 
@end

