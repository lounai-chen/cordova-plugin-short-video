cordova.define("cordova-plugin-short-video.ShortVideoPlugin", function(require, exports, module) {
var exec = require('cordova/exec');

var ShortVideoPlugin = { 
    init: function(
        success,
        error        
    ) {
       exec(success, error, 'ShortVideoPlugin', 'init', ['']); 
    },
    start_record: function(
        success,
        error        
    ) {
        cordova.require('cordova/channel').onCordovaReady.subscribe(function(){
            exec(success, error, 'ShortVideoPlugin', 'start_record', ['']);
        });
    }
    ,
        stop_record: function(
            success,
            error
        ) {
           exec(success, error, 'ShortVideoPlugin', 'stop_record', ['']);
        }
        ,
        switch_camera: function(
            success,
            error
        ) {
           exec(success, error, 'ShortVideoPlugin', 'switch_camera', ['']);
        }
        ,
        open_beaut: function(
            success,
            error
        ) {
           exec(success, error, 'ShortVideoPlugin', 'open_beaut', ['']);
        }
        ,
        close_beaut: function(
            success,
            error
        ) {
           exec(success, error, 'ShortVideoPlugin', 'close_beaut', ['']);
        }
}

module.exports = ShortVideoPlugin
});
