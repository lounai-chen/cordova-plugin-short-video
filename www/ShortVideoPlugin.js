 
var exec = require('cordova/exec');

var ShortVideoPlugin = { 
    init: function(
        success,
        error        
    ) {
       exec(success, error, 'ShortVideoPlugin', 'init', ['']); 
    },
    //开始录制
    start_record: function(
        success,
        error        
    ) {
        cordova.require('cordova/channel').onCordovaReady.subscribe(function(){
            exec(success, error, 'ShortVideoPlugin', 'start_record', ['']);
        });
    },
    //停止录制
    stop_record: function(
            success,
            error
        ) {
           exec(success, error, 'ShortVideoPlugin', 'stop_record', ['']);
        },
    //切换摄像头
    switch_camera: function(
            success,
            error
        ) {
           exec(success, error, 'ShortVideoPlugin', 'switch_camera', ['']);
        },
    //开启内置美颜
    open_beaut: function(
            success,
            error
        ) {
           exec(success, error, 'ShortVideoPlugin', 'open_beaut', ['']);
        },
    //关闭内置美颜
    close_beaut: function(
            success,
            error
        ) {
           exec(success, error, 'ShortVideoPlugin', 'close_beaut', ['']);
        },
    //视频缩略图
    //VideoSourcePath : 视频路径
    //VideoTime : 当前时间的取缩略图, 单位：微秒  
    video_thumbnail: function(
        VideoSourcePath,
        VideoTime,
        success,
        error
    ) {
       exec(success, error, 'ShortVideoPlugin', 'video_thumbnail', [VideoSourcePath,VideoTime]);
    },
    //视频剪辑
    //inputPath : 视频路径
    //startTime : 开始剪辑的时间, 单位：微秒  
    //endTime : 结束剪辑的时间,单位：微秒  
    video_clip: function(
        inputPath,
        startTime,
        endTime,
        success,
        error
    ) {
       exec(success, error, 'ShortVideoPlugin', 'video_clip', [inputPath,startTime,endTime]);
    },
	//销毁视频录制
	video_destroy: function(
		success,
		error
	) {
	   exec(success, error, 'ShortVideoPlugin', 'video_destroy', ['']);
	}
}

module.exports = ShortVideoPlugin
 
