package com.huayu.noah;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;

import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
 
import androidx.annotation.RequiresApi;

import com.aliqin.mytel.uitls.PermissionUtils;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;


/**
 * Created by noah chen on 2022/10/10.
 */

public class ShortVideoPlugin extends CordovaPlugin {

  private Context mActContext;
  private static CallbackContext mCallbackContext;
  private  ShortVideoFragment  fragment_short_video;
  private int containerViewId = 21; //<- set to random number to prevent conflict with other plugins
  private ViewParent webViewParent;

  @RequiresApi(api = Build.VERSION_CODES.M)
  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
    if (Build.VERSION.SDK_INT >= 23) {
      PermissionUtils.checkAndRequestPermissions(this.cordova.getActivity(), 10001, Manifest.permission.WRITE_EXTERNAL_STORAGE,
              Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE);
    }
    mActContext = this.cordova.getActivity().getApplicationContext();
    ApplicationInfo applicationInfo = null;
    try {
      applicationInfo = mActContext.getPackageManager().getApplicationInfo(mActContext.getPackageName(),
        PackageManager.GET_META_DATA);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  @Override
  public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
    //初始化
    if (action.equals("init")) {
      ShortVideoFragment.mAppContext = this.cordova.getContext();
      initShortVideo(callbackContext);
      return true;
    }
    //开始录播
    else if (action.equals("start_record")) {
      mCallbackContext = callbackContext;    //拿到回调对象并保存
      cordova.getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          //开始录制
          fragment_short_video.startRecording();
          callJS("success");
        }
      });
      return true;
    }
    //停止录播
    else if (action.equals("stop_record")) {
      mCallbackContext = callbackContext;    //拿到回调对象并保存
      cordova.getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          //停止录播
          fragment_short_video.stopRecording();
          callJS(ShortVideoFragment.videoPath);
        }
      });
      return true;
    }
    //切换摄像头
    else if (action.equals("switch_camera")) {
      mCallbackContext = callbackContext;    //拿到回调对象并保存
      cordova.getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          //切换摄像头
          fragment_short_video.switchCamera();
          callJS("success");
        }
      });
      return true;
    }
    //开启美颜
    else if (action.equals("open_beaut")) {
      mCallbackContext = callbackContext;    //拿到回调对象并保存
      cordova.getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          //开启美颜
          fragment_short_video.openBeaut();
          callJS("success");
        }
      });
      return true;
    }
    //关闭美颜
    else if (action.equals("close_beaut")) {
      mCallbackContext = callbackContext;    //拿到回调对象并保存
      cordova.getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          //关闭美颜
          fragment_short_video.closeBeaut();
          callJS("success");
        }
      });
      return true;
    }
    //视频缩略图
    else if (action.equals("video_thumbnail")) {
      mCallbackContext = callbackContext;
      String VideoSourcePath = args.getString(0);           // 视频路径
      long VideoTime  = Long.parseLong(args.getString(1));  // 当前时间的取缩略图, 单位：毫秒
      cordova.getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          String output = fragment_short_video.ThumbnailTetcher(VideoSourcePath, VideoTime);
          //callJS(output);
        }
      });
      return true;
    }
    //视频剪辑
    else if (action.equals("video_clip")) {
      mCallbackContext = callbackContext;
       String inputPath = args.getString(0);                // 视频路径
       long startTime = Long.parseLong(args.getString(1));  // 开始剪辑的时间, 单位：微秒
       long endTime = Long.parseLong(args.getString(2));    // 结束剪辑的时间,单位：微秒
      cordova.getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          String output = fragment_short_video.InitICrop(inputPath, startTime, endTime);
          //callJS(output);
        }
      });
      return true;
    }
    //销毁视频录制
    else if (action.equals("video_destroy")) {
      mCallbackContext = callbackContext;
      cordova.getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          fragment_short_video.destroyRecorder();
          fragment_short_video = null;
          callJS("sucess");
        }
      });
      return true;
    }
    return false;
  }

  public static void callJS(String message) {
    if (mCallbackContext != null) {
      PluginResult dataResult = new PluginResult(PluginResult.Status.OK, message);
      dataResult.setKeepCallback(true);// 非常重要
      mCallbackContext.sendPluginResult(dataResult);
    }
  }

  private boolean initShortVideo(CallbackContext callbackContext) {

    if (fragment_short_video != null) {
      callbackContext.error("fragment_short_video already started");
      return true;
    }

    fragment_short_video = new ShortVideoFragment();

    cordova.getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {

        //create or update the layout params for the container view
        FrameLayout containerView = (FrameLayout)cordova.getActivity().findViewById(containerViewId);
        if(containerView == null){
          containerView = new FrameLayout(cordova.getActivity().getApplicationContext());
          containerView.setId(containerViewId);

          FrameLayout.LayoutParams containerLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
          cordova.getActivity().addContentView(containerView, containerLayoutParams);
        }

        //display camera below the webview

        View view = webView.getView();
        ViewParent rootParent = containerView.getParent();
        ViewParent curParent = view.getParent();

        view.setBackgroundColor(0x00000000);


          // If parents do not match look for.
          if (curParent.getParent() != rootParent) {
            while (curParent != null && curParent.getParent() != rootParent) {
              curParent = curParent.getParent();
            }

            if (curParent != null) {
              ((ViewGroup) curParent).setBackgroundColor(0x00000000);
              ((ViewGroup) curParent).bringToFront();
            } else {
              // Do default...
              curParent = view.getParent();
              webViewParent = curParent;
              ((ViewGroup) view).bringToFront();
            }
          } else {
            // Default
            webViewParent = curParent;
            ((ViewGroup) curParent).bringToFront();
          }


        //add the fragment to the container
        FragmentManager fragmentManager = cordova.getActivity().getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(containerView.getId(), fragment_short_video);
        fragmentTransaction.commit();
      }
    });

    return true;
  }




}
