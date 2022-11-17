package com.huayu.noah;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.content.Context;
import android.content.DialogInterface;

import androidx.fragment.app.Fragment;

import android.os.FileUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aliqin.mytel.uitls.PermissionUtils;
import com.aliyun.common.utils.ThreadUtils;
import com.aliyun.svideosdk.AlivcSdkCore;
import com.aliyun.svideosdk.common.AliyunIThumbnailFetcher;
import com.aliyun.svideosdk.common.callback.recorder.OnRecordCallback;
import com.aliyun.svideosdk.common.impl.AliyunThumbnailFetcherFactory;
import com.aliyun.svideosdk.common.struct.common.MediaType;
import com.aliyun.svideosdk.common.struct.common.VideoDisplayMode;
import com.aliyun.svideosdk.common.struct.common.VideoQuality;
import com.aliyun.svideosdk.common.struct.effect.EffectFilter;
import com.aliyun.svideosdk.common.struct.encoder.VideoCodecs;
import com.aliyun.svideosdk.common.struct.recorder.MediaInfo;
import com.aliyun.svideosdk.crop.AliyunICrop;
import com.aliyun.svideosdk.crop.CropCallback;
import com.aliyun.svideosdk.crop.CropParam;
import com.aliyun.svideosdk.crop.impl.AliyunCropCreator;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


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
                        //结束录制，并且将录制片段视频拼接成一个视频
                        mAliyunRecord.finishRecording();
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
                        Log.e("outputPath", "输出路径是outputPath:" + outputPath);
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

    //视频剪辑
    public   void InitICrop(String outputPath,String inputPath,long startTime,long endTime){

        if("".equals(outputPath)){
            outputPath = Constants.SDCardConstants.getDir(mAppContext.getApplicationContext()) + File.separator + System.currentTimeMillis() + "-output.mp4";
        }
        if("".equals(inputPath)){
            inputPath = videoPath;
        }

        if(new File(inputPath).exists() == false){
            Log.i("InitICrop", "InitICrop,剪辑,输入文件不存在:" + inputPath);
        }

        //创建裁剪实例
        AliyunICrop aliyunICrop  = AliyunCropCreator.createCropInstance(mAppContext);

        //必要参数-输出视频宽高，源文件路径，输出文件路径
        int outputWidth = 720;
        int outputHeight = 1080;

        //设置裁剪参数
        CropParam cropParam = new CropParam();
        //裁剪后宽度，单位：像素
        cropParam.setOutputWidth(720);
        //裁剪后高度，单位：像素
        cropParam.setOutputHeight(1080);
        //设置裁剪模式，Scale：等比缩放裁剪， Fill：填充模式
        cropParam.setScaleMode(VideoDisplayMode.SCALE);

        //输出文件路径
        cropParam.setOutputPath(outputPath);
        //视频源文件路径
        cropParam.setInputPath(inputPath);

        //开始时间，单位：微秒
        cropParam.setStartTime(startTime);
        //结束时间，单位：微秒
        cropParam.setEndTime(endTime);

        //裁剪媒体类型，包括图片、视频、音频
        cropParam.setMediaType(MediaType.ANY_VIDEO_TYPE);
        //裁剪矩阵
        int startCropPosX = 0;
        int startCropPoxY = 0;
        Rect cropRect = new Rect(startCropPosX, startCropPoxY, startCropPosX + outputWidth,
            startCropPoxY + outputHeight);
        cropParam.setCropRect(cropRect);
        //帧率
        cropParam.setFrameRate(30);
        //gop
        cropParam.setGop(5);
        //视频质量
        cropParam.setQuality(VideoQuality.HD);
        //视频编码方式
        cropParam.setVideoCodec(VideoCodecs.H264_HARDWARE); //-20010002 错误的转码参数。
        //填充颜色
        cropParam.setFillColor(Color.BLACK);
        aliyunICrop .setCropParam(cropParam);


        //3设置回调
        aliyunICrop .setCropCallback(new CropCallback() {
            @Override
            public void onProgress(int i) {
                //裁剪进度
            }


            @Override
            public void onError(int i) {
                //错误代码
                Log.i("TestCrop", "testCrop,错误裁剪onError:" + i);
            }

            @Override
            public void onComplete(long executeTime) {
                //裁剪完成
                Log.i("TestCrop", "裁剪完成，耗时为:" + executeTime);
                Toast.makeText(mAppContext, "裁剪完成，耗时为:" + executeTime, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelComplete() {
            }
        });

        //4.开始裁剪
        aliyunICrop.startCrop();
    }


    //视频缩略图
    public  void  ThumbnailTetcher(String VideoSourcePath){
        if("".equals(VideoSourcePath)){
            VideoSourcePath = videoPath;
        }

        if(new File(VideoSourcePath).exists() == false){
            Log.i("ThumbnailTetcher", "ThumbnailTetcher, 输入文件不存在:" + VideoSourcePath);
        }

        //1.创建实例
        ////AlivcSdkCore.register(getApplicationContext()); 创建缩略图之前，要确认SDK有进行初始化
        AliyunIThumbnailFetcher fetcher = AliyunThumbnailFetcherFactory.createThumbnailFetcher();

        //2.2方式二：手动添加
        //添加视频
        fetcher.addVideoSource(VideoSourcePath);//, startTimeMills1, endTimeMills1, overlapDurationMills1);
        //添加图片
        //fetcher.addImageSource(path2, startTimeMills2, endTimeMills2, overlapDurationMills2);

        //3.设置缩略图输出大小等信息
        //cropMode
        AliyunIThumbnailFetcher.CropMode cropMode = AliyunIThumbnailFetcher.CropMode.Mediate;// 图像裁剪方式
        VideoDisplayMode videoDisplayMode = VideoDisplayMode.SCALE; //图像填充方式
        int cacheSize = 10; //缓存的缩略图数量
        fetcher.setParameters(720, 1080,cropMode, videoDisplayMode, cacheSize);
        //一般建议，如果是请求一系列缩略图，fastMode设置为true，会快速出图；如果是请求一张或者需要精确地时间点的缩略图，可以设置为false
        fetcher.setFastMode(true);

        //4.最终缩略图会在回调中给出
        long[] times = {1000, 2000, 3000}; //缩略图时间戳
        fetcher.requestThumbnailImage( times, new AliyunIThumbnailFetcher.OnThumbnailCompletion() {

            @Override
            public void onThumbnailReady(Bitmap frameBitmap, long time, int index) {
                //返回的缩略图
                saveBitmap(frameBitmap,time+""+index);
            }

            @Override
            public void onError(int errorCode) {
                //错误码信息
                Log.i("TestCrop", "testCrop,错误缩略1图onError:" + errorCode);
            }
        });

        //或者使用requestThumbnailImage(count,callback)会按照时间均分请求count张数的缩略图
        /*

        fetcher.requestThumbnailImage(10, new AliyunIThumbnailFetcher.OnThumbnailCompletion() {
            @Override
            public void onThumbnailReady(Bitmap frameBitmap, long time, int index) {
                // time是时间戳，对应之前传入的times里面的一个值，index是传入时间片的数组下标
                saveBitmap(frameBitmap,time+""+index);
            }

            @Override
            public void onError(int errorCode) {
                //错误码信息
                Log.i("TestCrop", "testCrop,错误缩略2图onError:" + errorCode);
            }
        });

         */

        //5.用完后销毁
        //fetcher.release();
    }

    //开始录制
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

        //添加背景颜色
        //mAliyunRecord  .setBackgroundColor(int color);
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

    //视频缩略图
    public  void  videoOpenBeaut(){
        ThumbnailTetcher("");
    }


    //视频剪辑
    public  void  videoInitICrop(){
        InitICrop("","",0,2233);
    }




    static String saveBitmap(Bitmap bm,String name) {
        String name_tmp = Constants.SDCardConstants.getDir(mAppContext.getApplicationContext()) + File.separator + System.currentTimeMillis() + "-thumbnail.jpg";
        File saveFile = new File(name_tmp);
        try {
            FileOutputStream saveImgOut = new FileOutputStream(saveFile);
            // compress - 压缩
            bm.compress(Bitmap.CompressFormat.JPEG, 80, saveImgOut);
            //存储完成后需要清除相关的进程
            saveImgOut.flush();
            saveImgOut.close();
            //Log.d("Save Bitmap", "The picture is save to your phone!"+name_tmp);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return name_tmp;
    }



}