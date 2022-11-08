cordova 插件,集成了阿里云短视频SDK有短视频录制等功能 

``` 
ionic cordova plugin add https://github.com/lounai-chen/cordova-plugin-short-video 
``` 
 

 




1 安卓: 


1.1 没自动配置License,需要在 AndroidManifest.xml 手动加入
``` 

``` 

2 IOS 
配置License
获取到License后，需要按以下操作配置License文件。License的获取及详细信息请参见获取短视频SDK License。

把下载的License文件导入到App工程中，在Info.plist文件中添加两个key，第一个key为AlivcLicenseKey，value为LicenseKey的值；第二个key为AlivcLicenseFile，value为内置证书文件（相对于mainBundle）的路径。示例如下所示：
key	value
AlivcLicenseKey	LicenseKey的值。取值示例：MoCTfuQ391Z01mNqG8f8745e23c8a457a8ff8d5faedc1****
AlivcLicenseFile	内置证书文件（相对于mainBundle）的路径。



3 阿里云的参考链接 

https://help.aliyun.com/document_detail/94459.html 

https://help.aliyun.com/document_detail/92854.html


