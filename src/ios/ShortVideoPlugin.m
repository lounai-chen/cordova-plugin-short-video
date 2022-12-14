/********* ShortVideoPlugin.m Cordova Plugin Implementation *******/
 
#import <Cordova/CDV.h>
  
#import <AVKit/AVKit.h>
#import <Foundation/Foundation.h>
#import <CommonCrypto/CommonCrypto.h>
 
#import "AliyunPathManager.h"
#import <AliyunVideoSDKPro/AliyunVideoSDKPro.h>
#import <AliyunVideoSDKPro/AliyunIRecorder.h>
#import <AliyunVideoSDKPro/AliyunErrorCode.h>
#import <AliyunvideoSDKPro/AliyunCrop.h>
#import <AliyunVideoSDKPro/AliyunNativeParser.h>

@interface ShortVideoPlugin : CDVPlugin <AliyunIRecorderDelegate,AliyunCropDelegate,AliyunThumbnailParserDelegate>

@property (nonatomic ,strong)  AliyunIRecorder *aliyunRecorder ;
@property (nonatomic,strong) AliyunCrop *cutPanel;
@property (nonatomic,strong) AliyunThumbnailParser *parser; //视频缩略图   AliyunNativeParser

- (void)init:(CDVInvokedUrlCommand*)command;
- (void)start_record:(CDVInvokedUrlCommand*)command;
- (void)stop_record:(CDVInvokedUrlCommand*)command;

@end

@implementation ShortVideoPlugin{
    NSString *webUrlString;
    NSString *cropOutPutPath;
}

static NSString* myAsyncCallBackId = nil;
static CDVPluginResult *pluginResult = nil;
static ShortVideoPlugin *selfplugin = nil;



- (void)pluginInitialize {
    //CDVViewController *viewController = (CDVViewController *)self.viewController;
    
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
   // dispatch_async(dispatch_get_main_queue(), ^{
        selfplugin = self;
        myAsyncCallBackId = command.callbackId;
        
        
        //清除之前生成的录制路径
        NSString *recordDir = [AliyunPathManager createRecrodDir];
        [AliyunPathManager makeDirExist:recordDir];
        //生成这次的存储路径
        NSString *taskPath = [recordDir stringByAppendingPathComponent:[AliyunPathManager randomString]];
        //视频存储路径
        NSString *videoSavePath = [[taskPath stringByAppendingPathComponent:[AliyunPathManager randomString]] stringByAppendingPathExtension:@"mp4"];
        
        CGSize resolution = CGSizeMake(720, 1280);           // 720P
        //初始化 视频录制
        AliyunIRecorder *recorder = [[AliyunIRecorder alloc] initWithDelegate:self videoSize:resolution];
        recorder.taskPath = taskPath;//设置文件夹路径
        recorder.preview = self.webView;  // self.videoView;//设置预览视图
        recorder.recordFps = 30;
        recorder.GOP = recorder.recordFps * 3;  // 预计隔3s一个关键帧
        recorder.outputPath = videoSavePath;//[taskPath stringByAppendingPathComponent:@"output.mp4"];//设置录制视频输出路径
        recorder.frontCaptureSessionPreset = AVCaptureSessionPreset1280x720;
        recorder.backCaptureSessionPreset = AVCaptureSessionPreset1280x720;
        self->webUrlString = recorder.outputPath;
        // 录制片段设置
        recorder.clipManager.deleteVideoClipsOnExit = NO; // 退出时自动删除所有片段，也可以考虑在拍摄结束后自行删除taskPath
        recorder.clipManager.maxDuration = 15;
        recorder.clipManager.minDuration = 3;
        
        self.aliyunRecorder = recorder;
        
        //开启预览
        [self.aliyunRecorder startPreview];
        
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
        
        
     
    //});
    
}



- (void)start_record:(CDVInvokedUrlCommand *)command
{
    selfplugin = self;
    myAsyncCallBackId = command.callbackId;

  // dispatch_async(dispatch_get_main_queue(), ^{
    // 开始录制一段视频
    [self.aliyunRecorder startRecording];
  // });
}


- (void)stop_record:(CDVInvokedUrlCommand *)command
{
    selfplugin = self;
    myAsyncCallBackId = command.callbackId;
    

       [self.aliyunRecorder stopRecording];

       
       [self.aliyunRecorder finishRecording];

    
}



//#pragma mark - record delegate-
// 录制进度
- (void)recorderVideoDuration:(CGFloat)duration{
    NSLog(@"录制中：%f",duration);

}

// 录制停止
- (void)recorderDidStopRecording{
    NSLog(@"----停止录制");

    [self.aliyunRecorder finishRecording];


}

- (void)recorderDidFinishRecording{
    NSLog(@"----完成录制");

    //停止预览
    [self.aliyunRecorder stopPreview];

    [self sendCmd:webUrlString];

}

- (void)recorderDeviceAuthorization:(AliyunIRecorderDeviceAuthor)status {
    //dispatch_async(dispatch_get_main_queue(), ^{
           if (status == AliyunIRecorderDeviceAuthorAudioDenied) {
               //[self showAVAuthorizationAlertWithMediaType:AVMediaTypeAudio];
           } else if (status == AliyunIRecorderDeviceAuthorVideoDenied) {
              // [self showAVAuthorizationAlertWithMediaType:AVMediaTypeVideo];
           }
           //当权限有问题的时候，不会走startPreview，所以这里需要更新下UI
          // [self.sliderButtonsView setSwitchRationButtonEnabled:(self.recorderDuration == 0)];
     //  });
}


- (void)switch_camera:(CDVInvokedUrlCommand *)command
{
    selfplugin = self;
    myAsyncCallBackId = command.callbackId;

   dispatch_async(dispatch_get_main_queue(), ^{
    
       [self.aliyunRecorder switchCameraPosition];
       [self sendCmd:@"success"];
   });
    
}

- (void)open_beaut:(CDVInvokedUrlCommand *)command
{
    selfplugin = self;
    myAsyncCallBackId = command.callbackId;

   dispatch_async(dispatch_get_main_queue(), ^{

       self.aliyunRecorder.beautifyValue = 89;
       self.aliyunRecorder.beautifyStatus = YES;
       [self sendCmd:@"success"];
   });
    
}


- (void)close_beaut:(CDVInvokedUrlCommand *)command
{
    selfplugin = self;
    myAsyncCallBackId = command.callbackId;

   dispatch_async(dispatch_get_main_queue(), ^{
    
       self.aliyunRecorder.beautifyStatus = NO;
       [self sendCmd:@"success"];
   });
    
}


//#pragam mark 视频剪辑
- (void) video_clip:(CDVInvokedUrlCommand *)command
{
    selfplugin = self;
    myAsyncCallBackId = command.callbackId;
    
    NSString* inputPath = [command.arguments objectAtIndex:0];
    int startTime =  [[command.arguments objectAtIndex:1] intValue];
    int endTime = [[command.arguments objectAtIndex:2] intValue];
    
     if([inputPath  isEqual: @""]){
         inputPath = webUrlString;
     }
    NSString *recordDir = [AliyunPathManager createRecrodDir];
    [AliyunPathManager makeDirExist:recordDir];
    //生成这次的存储路径
    NSString *taskPath = [recordDir stringByAppendingPathComponent:[AliyunPathManager randomString]];
    //视频存储路径
    NSString *videoSavePath = [[taskPath stringByAppendingPathComponent:[AliyunPathManager randomString]] stringByAppendingPathExtension:@"mp4"];
    
    
    //初始化 视频剪辑
    if(self.cutPanel){
        [self.cutPanel cancel];
    }
    self.cutPanel = [[AliyunCrop alloc] initWithDelegate:self];
    
    self->cropOutPutPath = videoSavePath;
    
    self.cutPanel.inputPath = inputPath;
    self.cutPanel.outputPath = videoSavePath;
    
    self.cutPanel.startTime =   startTime / 1000000; //微秒 -> 秒
    self.cutPanel.endTime = endTime / 1000000;
    
    //输出视频分辨率
    self.cutPanel.outputSize = CGSizeMake(720, 720);

    //裁剪区域
    self.cutPanel.rect = CGRectMake(0, (1280-720) / 2, 720, 720);

    //裁剪模式
    self.cutPanel.cropMode = AliyunCropModeScaleAspectCut;

    //码率bps
    self.cutPanel.bitrate = 1000 * 1000;

    //帧率
    self.cutPanel.fps = 30;

    //关键帧间隔
    self.cutPanel.gop = 90;

    //视频质量
    self.cutPanel.videoQuality = AliyunVideoQualityHight;

    //设置编码模式为硬编
    self.cutPanel.encodeMode = 1;
    
    //开始剪辑
    [self.cutPanel startCrop];
    
}

// - 视频缩略图
- (void) video_thumbnail:(CDVInvokedUrlCommand *)command
{
    selfplugin = self;
    myAsyncCallBackId = command.callbackId;
    
    NSString* videoPath = [command.arguments objectAtIndex:0];
    NSString* strStartTime =  [command.arguments objectAtIndex:1];
    NSNumber* startTime = @([strStartTime longLongValue] / 1000.0);

    if([videoPath  isEqual: @""]){
        videoPath = webUrlString;
    }
    
    //1.初始化
    self.parser = [[AliyunThumbnailParser alloc]initWithPath:videoPath delegate:self];

    //2.设置图片参数
    //图片裁剪范围
    [self.parser setCutFrame:CGRectMake(0, 0, 200, 200)];

    //图片输出大小
    CGFloat ouputSizeWidth = 200;
    [self.parser setOutputSize:CGSizeMake(ouputSizeWidth, ouputSizeWidth)];

    //3.设置缩略图的时间点
    NSArray<NSNumber *> *timeList = @[startTime];  //单位秒
    [self.parser addThumbnailTimeList:timeList];

    //4.开始获取缩略图
    [self.parser start];


}

//5.获取缩略图回调
// 获取出错，注意在子线程回调
- (void)thumbnailParser:(AliyunThumbnailParser *)parser onError:(int)code {
}

//获取某张图片出错，注意在子线程回调
- (void)thumbnailParser:(AliyunThumbnailParser *)parser onPicError:(int)code time:(float)time {
}

//获取到的图片，注意在子线程回调
- (void)thumbnailParser:(AliyunThumbnailParser *)parser onGetPicture:(UIImage *)image time:(float)time {
    NSString  *imageSavePath = [self getImagePath:image];
    [self sendCmd:imageSavePath];
}

//照片获取本地路径转换
-(NSString *)getImagePath:(UIImage *)Image {
    
    NSString * filePath = nil;
    NSData * data = nil;
    
    if (UIImagePNGRepresentation(Image) == nil) {
        data = UIImageJPEGRepresentation(Image, 0.5);
    } else {
        data = UIImagePNGRepresentation(Image);
    }
    
    //图片保存的路径
    //这里将图片放在沙盒的documents文件夹中
    NSString * DocumentsPath = [NSHomeDirectory() stringByAppendingPathComponent:@"Documents"];
    
    //文件管理器
    NSFileManager *fileManager = [NSFileManager defaultManager];
    
    //把刚刚图片转换的data对象拷贝至沙盒中
    [fileManager createDirectoryAtPath:DocumentsPath withIntermediateDirectories:YES attributes:nil error:nil];
    
    CFUUIDRef puuid = CFUUIDCreate(nil);
    CFStringRef uuidString = CFUUIDCreateString(nil, puuid);
    NSString * file_result = (NSString *)CFBridgingRelease(CFStringCreateCopy( NULL, uuidString));
    CFRelease(puuid);
    CFRelease(uuidString);

    file_result = [@"/" stringByAppendingString:file_result];
    NSString * ImagePath = [file_result stringByAppendingString:@"-temp.png"];
    [fileManager createFileAtPath:[DocumentsPath stringByAppendingString:ImagePath] contents:data attributes:nil];
    
    //得到选择后沙盒中图片的完整路径
    filePath = [[NSString alloc]initWithFormat:@"%@%@",DocumentsPath,ImagePath];
    
    return filePath;
}

//获取图片完成，注意在子线程回调
- (void)thumbnailParserOnCompleted:(AliyunThumbnailParser *)parser {
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
 

#pragma  mark - AliyunCropDelegate
- (void)cropOnError:(int)error {
  
}


//剪辑完成 回掉函数
 
 //裁剪进度
- (void)cropTaskOnProgress:(float)progress {
}

//裁剪完成
- (void)cropTaskOnComplete {
    NSLog(@"----完成裁剪");
    [self sendCmd: cropOutPutPath];
}

//裁剪取消
- (void)cropTaskOnCancel {
}

 

@end

