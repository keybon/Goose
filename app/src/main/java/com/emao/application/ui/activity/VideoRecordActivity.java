package com.emao.application.ui.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.emao.application.R;
import com.emao.application.ui.utils.CommonUtils;
import com.emao.application.ui.utils.ToastUtils;
import com.emao.application.ui.view.LongTouchBtn;

import java.io.File;
import java.io.IOException;

/**
 * @author keybon
 */

public class VideoRecordActivity extends BaseActivity implements View.OnClickListener, SurfaceHolder.Callback, MediaRecorder.OnErrorListener {

    public static final int REQUEST_VIDEO = 0x00002;
    public static final int REQUEST_VIDEO_CANCEL = 0x00003;
    public static final String REQUEST_VIDEO_PATH = "REQUEST_VIDEO_PATH";

    private SurfaceView surfaceView;
    private ImageView video_focus, videoDelete, videoSure;
    private LongTouchBtn btnStart;
    private RelativeLayout video_rl;
    private ProgressBar progressbar;
    //UI
    private SurfaceHolder mSurfaceHolder;
    private SurfaceHolder playerHolder;
    // 标记，判断当前是否正在录制
    private boolean isRecording;

    // 存储文件
    private Camera mCamera;
    private MediaRecorder mediaRecorder;
    private String currentVideoFilePath;
    private MediaPlayer mediaPlayer;

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        try {
            if (mediaRecorder != null) {
                mediaRecorder.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_video_record);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        super.initView();
        setVideovisibility(true);
        setActionBarTitle(CommonUtils.getResourceString(R.string.picture_release_title));
        surfaceView = findViewById(R.id.video_surfaceview);
        video_focus = findViewById(R.id.video_record_focus);
        btnStart = findViewById(R.id.video_record_start);
        videoDelete = findViewById(R.id.video_record_deletet);
        videoSure = findViewById(R.id.video_record_sure);
        video_rl = findViewById(R.id.video_rl);
        progressbar = findViewById(R.id.video_progress);

        videoDelete.setOnClickListener(this);
        videoSure.setOnClickListener(this);
        video_rl.setOnClickListener(this);

        mediaPlayer = new MediaPlayer();

        initSurfaceHolder();

    }

    private int mUnmber = 10;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (mUnmber < 100) {
                    progressbar.setProgress(mUnmber);
                    Log.e("keybon", "mUnmber == " + mUnmber);
                    handler.sendEmptyMessageDelayed(1, 1000);
                    mUnmber += 10;
                } else if (mUnmber == 100) {
                    Log.e("keybon", "暂停录制 == ");
                    progressbar.setProgress(mUnmber);
                    stopRecord();
                }
            } else if (msg.what == 2) {
                progressbar.setProgress(mUnmber);
            } else if (msg.what == 3) {
                mUnmber = 10;
                progressbar.setProgress(0);
            }
        }
    };

    @Override
    public void initData() {
        super.initData();

        btnStart.setOnLongTouchListener(new LongTouchBtn.LongTouchListener() {
            @Override
            public void onLongDownTouch(long downTime) {
                handler.sendEmptyMessageDelayed(1, 0);
                btnStart.setBackground(getResources().getDrawable(R.drawable.ico_shooting_finished));
//                stopMediaPlayer();
                startRecord();
            }

            @Override
            public void onLongUpTouch(long downTime) {
                btnStart.setBackground(getResources().getDrawable(R.drawable.ico_shooting_act));
                handler.removeMessages(1);
                handler.sendEmptyMessage(2);
                stopRecord();
//                if(mCamera != null){
//                    mCamera.stopPreview();
//                }
                stopCamera();
                repeatPlay();
            }
        });
        initAnimation();
    }

    private void initAnimation() {

        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.2f);
        alphaAnimation.setDuration(500);
        alphaAnimation.setRepeatCount(1);
        video_focus.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                ScaleAnimation scaleAnimation = new ScaleAnimation(1, 0.5f, 1, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                scaleAnimation.setDuration(300);
                scaleAnimation.setRepeatCount(1);
                video_focus.startAnimation(scaleAnimation);
                scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        video_focus.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void initSurfaceHolder(){
        //配置SurfaceHodler
        mSurfaceHolder = surfaceView.getHolder();
        // 设置Surface不需要维护自己的缓冲区
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // 设置分辨率
//        mSurfaceHolder.setFixedSize(320, 280);
        // 设置该组件不会让屏幕自动关闭
        mSurfaceHolder.setKeepScreenOn(true);
        //回调接口
        mSurfaceHolder.addCallback(this);
    }

    /**
     * 初始化摄像头  1 前摄像头 2 后摄像头
     */

    private void initCamera() {
        if (mCamera != null) {
            stopCamera();
        }
        //默认启动后置摄像头
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        if (mCamera == null) {
            ToastUtils.showShortToast("未能获取到相机！");
            return;
        }
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            //配置CameraParams
            setCameraParams();
            //启动相机预览
            mCamera.startPreview();
        } catch (IOException e) {
        }
    }

    /**
     * 打开闪光灯
     */
    private void openFlashMode(int open) {
        if (mCamera == null) {
            //默认启动后置摄像头
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        }
        Camera.Parameters params = mCamera.getParameters();
        if (open == 1) {
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            flashLightFlag = true;
        } else {
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            flashLightFlag = false;
        }

        mCamera.setParameters(params);

    }

    private boolean cameraFlag = false;

    private void changeCamera(){

        mCamera.stopPreview();
        mCamera.release();


        int frontIndex =-1;
        int backIndex = -1;
        int cameraCount = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        for(int cameraIndex = 0; cameraIndex<cameraCount; cameraIndex++){
            Camera.getCameraInfo(cameraIndex, info);
            if(info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                frontIndex = cameraIndex;
            }else if(info.facing == Camera.CameraInfo.CAMERA_FACING_BACK){
                backIndex = cameraIndex;
            }
        }

        if(!cameraFlag && frontIndex != -1){
            cameraFlag = true;
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        }else {
            if(backIndex != -1){
                cameraFlag = false;
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            }
        }
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            //配置CameraParams
            setCameraParams();
            //启动相机预览
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 设置摄像头为竖屏
     */
    private void setCameraParams() {
        if (mCamera != null) {
            Camera.Parameters params = mCamera.getParameters();
            //设置相机的横竖屏(竖屏需要旋转90°)
            if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                params.set("orientation", "portrait");
                mCamera.setDisplayOrientation(90);
            } else {
                params.set("orientation", "landscape");
                mCamera.setDisplayOrientation(0);
            }
            //设置聚焦模式
            if(!cameraFlag){
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            //缩短Recording启动时间
            params.setRecordingHint(true);
            //影像稳定能力
            if (params.isVideoStabilizationSupported()) {
                params.setVideoStabilization(true);
            }
            mCamera.setParameters(params);
        }
    }

    /**
     * 释放摄像头资源
     */
    private void stopCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private boolean flashLightFlag = false;

    @Override
    public void flashLight(View v) {
        super.flashLight(v);
        if (!flashLightFlag) {
            openFlashMode(1);
        } else {
            openFlashMode(0);
        }
    }

    @Override
    public void turnCamera(View v) {
        super.turnCamera(v);
        changeCamera();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_record_deletet:

                if (!TextUtils.isEmpty(currentVideoFilePath)) {
                    File videoFile = new File(currentVideoFilePath);
                    if (videoFile.exists()) {
                        videoFile.delete();
                    }
                }
                stopMediaPlayer();
                initCamera();
                handler.sendEmptyMessage(3);
                break;
            case R.id.video_record_sure:

                stopCamera();
                Intent intent = new Intent();
                intent.putExtra(ReleaseContentActivity.RELEASE_FLAG,ReleaseContentActivity.VIDEO_TYPE);
                intent.putExtra(REQUEST_VIDEO_PATH, currentVideoFilePath);
                intent.setClass(this,ReleaseContentActivity.class);
                startActivity(intent);
//                setResult(REQUEST_VIDEO, intent);
                finish();

                break;
            case R.id.video_record_focus:

                break;
            case R.id.video_rl:
                initAnimation();
                break;
            default:
                break;
        }
    }

    /**
     * 开始录制视频
     */
    public void startRecord() {
//        initCamera();
        mCamera.unlock();
        setConfigRecord();
        try {
            //开始录制
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isRecording = true;
    }

    /**
     * 停止录制视频
     */
    public void stopRecord() {
        if (isRecording && mediaRecorder != null) {
            // 设置后不会崩
//            mediaRecorder.setOnErrorListener(null);
//            mediaRecorder.setPreviewDisplay(null);
            try {
                //停止录制
                mediaRecorder.stop();
            } catch (IllegalStateException ll) {
            }

            mediaRecorder.reset();
            //释放资源
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
        }
    }

    public void repeatPlay() {
        if (isRecording) {
            ToastUtils.showShortToast("正在录制，请结束录制再播放");
            return;
        }
        if (TextUtils.isEmpty(currentVideoFilePath)) {
            ToastUtils.showShortToast("视频地址无效");
            return;
        }
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.reset();

        Uri uri = Uri.parse(currentVideoFilePath);
        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mSurfaceHolder = surfaceView.getHolder();

        mediaPlayer.setDisplay(mSurfaceHolder);


        mediaPlayer.setLooping(true);
        try {

//            mediaPlayer.prepare();

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = MediaPlayer.create(this, uri);
            }

            mediaPlayer.start();

        } catch (Exception e) {
        }
    }

    /**
     * 配置MediaRecorder()
     */
    private void setConfigRecord() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.reset();
        mediaRecorder.setCamera(mCamera);
        mediaRecorder.setOnErrorListener(this);

        //使用SurfaceView预览
        mediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

        //1.设置采集声音
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        //设置采集图像
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        //2.设置视频，音频的输出格式 mp4
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        //3.设置音频的编码格式
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //设置图像的编码格式
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        //设置立体声
//        mediaRecorder.setAudioChannels(2);
//        设置最大录像时间 单位：毫秒
        mediaRecorder.setMaxDuration(10 * 1000);
        //设置最大录制的大小 单位，字节
//        mediaRecorder.setMaxFileSize(1024 * 1024);
        //音频一秒钟包含多少数据位
        CamcorderProfile mProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
//        mediaRecorder.setAudioEncodingBitRate(44100);
//        if (mProfile.videoBitRate > 2 * 1024 * 1024){
//            mediaRecorder.setVideoEncodingBitRate(2 * 1024 * 1024);
//        } else {
//            mediaRecorder.setVideoEncodingBitRate(1024 * 1024);
//        }
        mediaRecorder.setVideoEncodingBitRate(mProfile.videoBitRate);
        mediaRecorder.setVideoFrameRate(mProfile.videoFrameRate);

        //设置选择角度，顺时针方向，因为默认是逆向90度的，这样图像就是正常显示了,这里设置的是观看保存后的视频的角度
        mediaRecorder.setOrientationHint(90);
        //设置录像的分辨率
//        mediaRecorder.setVideoSize(352, 288);
        mediaRecorder.setVideoSize(640, 480);

        //设置录像视频保存地址
        currentVideoFilePath = CommonUtils.getVideoName();
        mediaRecorder.setOutputFile(currentVideoFilePath);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setVideovisibility(false);
        stopMediaPlayer();
    }

    /**
     * 暂停视频播放
     */
    public void stopMediaPlayer() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.setDisplay(null);
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceHolder = holder;
        if(!isRecording){
            initCamera();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mSurfaceHolder = holder;
//        if (mSurfaceHolder.getSurface() == null) {
//            return;
//        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopCamera();
    }

    @Override
    public void backKeyPress() {
        stopCamera();
        Intent intent = new Intent();
        intent.putExtra(ReleaseContentActivity.RELEASE_FLAG,ReleaseContentActivity.VIDEO_TYPE);
        intent.putExtra(REQUEST_VIDEO_PATH, currentVideoFilePath);
        intent.setClass(this,ReleaseContentActivity.class);
        startActivity(intent);
        this.finish();
    }
}
