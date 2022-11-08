cordova.define("cordova-plugin-short-video.ShortVideoPlugin", function(require, exports, module) {
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
        }
}

module.exports = ShortVideoPlugin
});
