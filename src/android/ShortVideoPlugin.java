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
