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
    },
    stop_record: function(
        success,
        error        
    ) {
       exec(success, error, 'ShortVideoPlugin', 'stop_record', ['']); 
    }
}

module.exports = ShortVideoPlugin