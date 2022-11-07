package com.huayu.noah;

import android.Manifest;
import android.os.Bundle;
import android.content.Context;
import android.content.DialogInterface;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.aliqin.mytel.uitls.PermissionUtils;
import com.aliyun.common.utils.ThreadUtils;
import com.aliyun.svideosdk.AlivcSdkCore;
import com.aliyun.svideosdk.common.callback.recorder.OnRecordCallback;
import com.aliyun.svideosdk.common.struct.effect.EffectFilter;
import com.aliyun.svideosdk.common.struct.recorder.MediaInfo;
import com.aliyun.svideosdk.recorder.AliyunIRecorder;
import com.aliyun.svideosdk.recorder.impl.AliyunRecorderCreator;
import com.zhongzilian.chestnutapp.R;


import com.aliyun.svideosdk.recorder.AliyunIRecorder;
import com.aliyun.svideosdk.recorder.impl.AliyunRecorderCreator;
import com.aliyun.svideosdk.AlivcSdkCore;
import com.aliyun.svideosdk.AlivcSdkCore.AlivcDebugLoggerLevel;
import com.aliyun.svideosdk.AlivcSdkCore.AlivcLogLevel;
import com.aliyun.svideosdk.recorder.AliyunIRecorder;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShortVideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShortVideoFragment  extends android.app.Fragment  {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    public static Context mAppContext;
    private String mParam1;
    private String mParam2;
    private View PageView;
    private Boolean sdkResult;
    private  static AliyunIRecorder mAliyunRecord   ;
    private  static SurfaceView mCameraViiew   ;

    public static  String videoPath;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShortVideoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShortVideoFragment newInstance(String param1, String param2) {
        ShortVideoFragment fragment = new ShortVideoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        sdkResult = AlivcSdkCore.register(mAppContext);
        //如果 sdkResult 返回false，表示License初始化失败，请检查License的配置是否正确。
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         PageView = inflater.inflate(R.layout.activity_short_video, container, false);
        //创建录制接口
        mAliyunRecord = AliyunRecorderCreator.getRecorderInstance(mAppContext);//参数context为当前页面的上下文

        MediaInfo mediaInfo = new MediaInfo();
        mediaInfo.setFps(30);
        mediaInfo.setCrf(6);
        mediaInfo.setVideoWidth(720);
        mediaInfo.setVideoHeight(1080);
        mAliyunRecord.setMediaInfo(mediaInfo);
        mCameraViiew = (SurfaceView)PageView.findViewById(R.id.cameraPreviewView);
        mAliyunRecord.setDisplayView(mCameraViiew);
        mAliyunRecord.startPreview();



        //设置滤镜  需自定义配置文件
       // mAliyunRecord.applyFilter(EffectFilter effectFilter);//参数路径设置为null表示移除滤镜效果

        //移除滤镜
        //mAliyunRecord.applyFilter(new EffectFilter(null));




        mAliyunRecord.setOnRecordCallback(new OnRecordCallback() {
            @Override
            public void onClipComplete(final boolean validClip, final long clipDuration) {


                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                    }
                });

            }

            /**
             * 合成完毕的回调
             * @param outputPath
             */
            @Override
            public void onFinish(final String outputPath) {


                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });

            }

            @Override
            public void onProgress(final long duration) {

                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });

            }

            @Override
            public void onMaxDuration() {

                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });

            }

            @Override
            public void onError(int errorCode) {

                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }

            @Override
            public void onInitReady() {

                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }

        });

        return PageView;
    }



    public void startRecording(){

        videoPath = Constants.SDCardConstants.getDir(mAppContext.getApplicationContext()) + File.separator + System.currentTimeMillis() + "-record.mp4";
        mAliyunRecord.setOutputPath(videoPath); // "/storage/emulated/0/DCIM/Camera/123.mp4" /storage/emulated/0/DCIM/Camera/
        mAliyunRecord.startRecording();
    }


    //结束录制
    public void stopRecording() {

        mAliyunRecord.stopRecording();

    }

    //切换摄像头
    public  void switchCamera(){
        //切换摄像头
        mAliyunRecord.switchCamera();
    }

    //开启美颜
    public  void  openBeaut(){
        //设置美颜开关
        mAliyunRecord.setBeautyStatus(true);

        //设置美颜程度 max = 100
        mAliyunRecord.setBeautyLevel(89);
    }


    //关闭美颜
    public  void  closeBeaut(){
        //设置美颜开关
        mAliyunRecord.setBeautyStatus(false);

    }
}