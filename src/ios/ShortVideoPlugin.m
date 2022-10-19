/********* ShortVideoPlugin.m Cordova Plugin Implementation *******/
 
#import <Cordova/CDV.h>
  
#import <AVKit/AVKit.h>
#import <Foundation/Foundation.h>
#import <CommonCrypto/CommonCrypto.h>
 

#import <AliyunVideoSDKPro/AliyunVideoSDKPro.h>
 

@interface ShortVideoPlugin : CDVPlugin {
    NSString *webUrlString;
} 
 
- (void)init:(CDVInvokedUrlCommand*)command;
- (void)start_record:(CDVInvokedUrlCommand*)command;
- (void)stop_record:(CDVInvokedUrlCommand*)command;

@end

@implementation ShortVideoPlugin

static NSString* myAsyncCallBackId = nil;
static CDVPluginResult *pluginResult = nil;
static ShortVideoPlugin *selfplugin = nil;

- (void)pluginInitialize {
    CDVViewController *viewController = (CDVViewController *)self.viewController;
   
}

- (void)init:(CDVInvokedUrlCommand *)command
{
    selfplugin = self;
    myAsyncCallBackId = command.callbackId;
 
    
    pluginResult = [CDVPluginResult resultWithStatus: CDVCommandStatus_NO_RESULT];
    [pluginResult setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:pluginResult callbackId: command.callbackId];
}


- (void)start_record:(CDVInvokedUrlCommand *)command
{
    selfplugin = self;
    myAsyncCallBackId = command.callbackId;    
  
}
 

- (void)stop_record:(CDVInvokedUrlCommand *)command
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
 
 
@end

