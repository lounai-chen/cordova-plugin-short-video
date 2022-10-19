/********* ShortVideoPlugin.m Cordova Plugin Implementation *******/
 
#import <Cordova/CDV.h>
  
#import <AVKit/AVKit.h>
#import <Foundation/Foundation.h>
#import <CommonCrypto/CommonCrypto.h>
 
#import "AliyunPathManager.h"
#import <AliyunVideoSDKPro/AliyunVideoSDKPro.h>
 

@interface ShortVideoPlugin : CDVPlugin {
    NSString *webUrlString;
    
} 
 @property (nonatomic ,strong)  AliyunIRecorder *aliyunRecorder ;

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

    
    //打印版本信息
    [AliyunVideoSDKInfo printSDKInfo];

    // ≥ 3.30.0版本：
    NSError *error = [AliyunVideoSDKInfo registerSDK]; // 返回error为nil表示注册成功。
    // 因为注册失败基本属于接入错误，所以建议直接加上Assert就可以在接入调试时显示错误和修复建议。
    //NSAssert2(error == nil, @"注册SDK失败！%@；%@", error.localizedDescription, error.localizedRecoverySuggestion);

    //代码主动查询鉴权结果。
   // AliyunVideoLicenseResultCode code = [AliyunVideoLicenseManager check];


}

- (void)init:(CDVInvokedUrlCommand *)command
{
        dispatch_async(dispatch_get_main_queue(), ^{
    selfplugin = self;
    myAsyncCallBackId = command.callbackId;
 
    
    NSString *editDir = [AliyunPathManager compositionRootDir];
    NSString *taskPath = [editDir stringByAppendingPathComponent:[AliyunPathManager randomString]];

    CGSize resolution = CGSizeMake(720, 1280);           // 720P
    AliyunIRecorder *recorder = [[AliyunIRecorder alloc] initWithDelegate:self videoSize:resolution];
    recorder.taskPath = taskPath;//设置文件夹路径
    recorder.preview = self.webView;  // self.videoView;//设置预览视图
    recorder.recordFps = 30;
    recorder.GOP = recorder.recordFps * 3;  // 预计隔3s一个关键帧
    recorder.outputPath = [taskPath stringByAppendingPathComponent:@"output.mp4"];//设置录制视频输出路径
    recorder.frontCaptureSessionPreset = AVCaptureSessionPreset1280x720;
    recorder.backCaptureSessionPreset = AVCaptureSessionPreset1280x720;

            webUrlString = recorder.outputPath;
    // 录制片段设置
    recorder.clipManager.deleteVideoClipsOnExit = YES; // 退出时自动删除所有片段，也可以考虑在拍摄结束后自行删除taskPath
    recorder.clipManager.maxDuration = 15;
    recorder.clipManager.minDuration = 3;

    self.aliyunRecorder = recorder;

// AliyunIRecorderCameraPositionFront 前摄像头启动预览
// AliyunIRecorderCameraPositionBack 后摄像头启动预览
[self.aliyunRecorder startPreviewWithPositon:AliyunIRecorderCameraPositionFront];

           
            // 插件 view 置顶 & 透明
           // [self.viewController addChildViewController:self.publisherVC];
            self.webView.opaque = NO;
            self.webView.backgroundColor = [UIColor clearColor];

           // [self.webView.superview addSubview:self.publisherVC.view];
            [self.webView.superview bringSubviewToFront:self.webView];

     myAsyncCallBackId = command.callbackId;
    pluginResult = [CDVPluginResult resultWithStatus: CDVCommandStatus_NO_RESULT];
    [pluginResult setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:pluginResult callbackId: command.callbackId];

 });
    

}


- (void)start_record:(CDVInvokedUrlCommand *)command
{
    selfplugin = self;
    myAsyncCallBackId = command.callbackId;    

   dispatch_async(dispatch_get_main_queue(), ^{
    // 开始录制一段视频
    [self.aliyunRecorder startRecording];
   });
}
 

- (void)stop_record:(CDVInvokedUrlCommand *)command
{
    selfplugin = self;
    myAsyncCallBackId = command.callbackId;    

   dispatch_async(dispatch_get_main_queue(), ^{
    //结束录制，并且将录制片段视频拼接成一个完整的视频
    [self.aliyunRecorder finishRecording];
       [self sendCmd:webUrlString];
   });
    
  
    
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

