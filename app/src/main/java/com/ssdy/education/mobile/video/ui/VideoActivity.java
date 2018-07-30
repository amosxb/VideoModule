package com.ssdy.education.mobile.video.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.ssdy.education.mobile.video.R;
import com.ssdy.education.mobile.video.event.HomeworkEvent;
import com.ssdy.education.mobile.video.event.MediaEvent;
import com.ssdy.education.mobile.video.utils.DisplayMetricsUtil;
import com.ssdy.education.mobile.video.utils.ToastUtil;
import com.ssdy.education.mobile.video.video.CameraManager;
import com.ssdy.education.mobile.video.video.DateTimeUtils;
import com.ssdy.education.mobile.video.video.MediaManager;
import com.ssdy.education.mobile.video.video.MyOrientationDetector;
import com.ssdy.education.mobile.video.video.RecorderManager;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * 描述：录像姐main控制
 * 作者：shaobing
 * 时间： 2016/11/28 17:08
 */
public class VideoActivity extends Activity implements View.OnClickListener, SurfaceHolder.Callback {

    public static final String OUTPUT_PATH = "output_path";
    private static final String TAG = "VideoActivity";
    /**
     * 确定返回前一个Activity的结果码，为了和照相区分，不用RESULT_OK
     */
    public static final int RESULTCODE = 1011;
    //图像预览
    private SurfaceView mCameraGLView;
    private SurfaceHolder surfaceHolder;
    //录像按钮
    private ImageView mRecordButton;
    //重新录制 取消
    private TextView mReStartButton;
    //定时时间
    private TextView tvTimerTxt;
    //播放录像
    private ImageView ivPlay;
    //使用按钮
    private ImageView useButton;

    private RelativeLayout rlytCamera;

    private ImageView customCameraShow;
    //记录当前的时间
    private long mCurrentTime;
    private boolean isRecording = false;
    //权限判断
    private boolean isPermisson;
    private Camera mCamera;
    private Camera.Size mCameraSize;
    private int mRotation;
    //接受前一个几面传过来的录像文件路径
    public static final String FILEPATH = "filepath";
    private String filePath = "";
    //判断横竖屏
    MyOrientationDetector myOrientationDetector;
    //
    private boolean isPlaying;

    private boolean isSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_video4_3);
        Bundle bundle = this.getIntent().getExtras();
        filePath = bundle.getString(FILEPATH);
        initView();
        initData();
        initEvent();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mCameraGLView = (SurfaceView) findViewById(R.id.cameraView);
        mRecordButton = (ImageView) findViewById(R.id.record_button);
        mReStartButton = (TextView) findViewById(R.id.restart_button);
        tvTimerTxt = (TextView) findViewById(R.id.tv_countdown);
        ivPlay = (ImageView) findViewById(R.id.iv_play);
        rlytCamera = (RelativeLayout) findViewById(R.id.rlyt_camera);
        customCameraShow = (ImageView) findViewById(R.id.custom_camera_show);
        useButton = (ImageView) findViewById(R.id.use_button);
    }

    private void initData() {
        float width = DisplayMetricsUtil.getDisplayWidth(VideoActivity.this);
        float height = DisplayMetricsUtil.getDisplayHeight(VideoActivity.this);
        mCamera = CameraManager.getInstance().init(VideoActivity.this, (int) height, (int) width, new CameraManager.OnCameraListener() {
            @Override
            public void OnPermisson(boolean flag) {
                ToastUtil.showLongToast(VideoActivity.this, getString(R.string.jurisdiction_camera_refuse));
                isPermisson = true;
            }

            @Override
            public void OnCameraSize(Camera.Size size) {
                mCameraSize = size;
            }

            @Override
            public void OnCameraRotation(int rotation) {
                mRotation = rotation;
            }
        });
        RecorderManager.getInstanse().init(new RecorderManager.OnAudioPermissionListener() {
            @Override
            public void OnAudioPermission(boolean flag) {
                ToastUtil.showLongToast(VideoActivity.this, getString(R.string.jurisdiction_record_refuse));
                isPermisson = true;
            }
        });
        surfaceHolder = mCameraGLView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        if (mCameraSize != null) {
            surfaceHolder.setFixedSize(mCameraSize.width, mCameraSize.height);
        }
        surfaceHolder.addCallback(this);
        try {
            if (filePath.isEmpty()) {
                filePath = Environment.getExternalStorageDirectory() + "/aaaaaa/" + System.currentTimeMillis() + ".mp4";
            }
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdir();
            }
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            filePath = file.getAbsolutePath();
        } catch (Exception e) {
        }
    }

    private void initEvent() {
        mRecordButton.setOnClickListener(this);
        mReStartButton.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        useButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        myOrientationDetector = new MyOrientationDetector(this);
        myOrientationDetector.enable();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myOrientationDetector.disable();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().postSticky(new HomeworkEvent(HomeworkEvent.CODE4, isSave, 0, filePath));
        CameraManager.getInstance().stopPreview();
        MediaManager.getInstance().stopVideo();
    }

    //进行倒计时计数（最大3分钟）
    private CountDownTimer countDownTimer = new CountDownTimer(1000 * 60 * 1, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            tvTimerTxt.setText(DateTimeUtils.getMMSS(1000 * 60 * 1 - millisUntilFinished));
        }

        @Override
        public void onFinish() {
            //听着录音
            tvTimerTxt.setText("1:00");

            //模拟点击停止按钮
            mRecordButton.performClick();
        }
    };

    /**
     * 开启定时器
     */
    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.start();
        }
        tvTimerTxt.setText("0:00");
    }

    /**
     * 关闭定时器
     */
    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.record_button: {
                if (!isRecording) {
                    if (isPermisson) {
                        return;
                    }
                    CameraManager.getInstance().start();
                    //在现有的基础上将屏幕旋转，保证旋转之后视频为正的
                    mRotation = CameraManager.getInstance().setRotation(VideoActivity.this, mCamera.getParameters(), null);
                    mRotation = mRotation + myOrientationDetector.getScreenOrientation();
                    MediaManager.getInstance().startVideo(mCamera, mCameraGLView.getHolder(), mCameraSize, mRotation, filePath);
                    mCurrentTime = System.currentTimeMillis();
                    handleView(0);
                    isRecording = true;
                    startTimer();
                } else {
                    if ((System.currentTimeMillis() - mCurrentTime) < 1000) {
                        ToastUtil.showLongToast(this, getString(R.string.video_time_hint));
                    } else {
                        //停止录像
                        isRecording = false;
                        MediaManager.getInstance().stopVideo();
                        CameraManager.getInstance().stop();
                        handleView(1);
                        stopTimer();
                        try {
                            getBitmapsFromVideo(filePath);

                        } catch (Exception e) {
                        }
                    }
                }
                break;
            }
            case R.id.use_button: {
                isSave = true;
                finish();
                break;
            }
            case R.id.restart_button: {
                CameraManager.getInstance().start();
                //在现有的基础上将屏幕旋转，保证旋转之后视频为正的
                mRotation = CameraManager.getInstance().setRotation(VideoActivity.this, mCamera.getParameters(), null);
                mRotation = mRotation + myOrientationDetector.getScreenOrientation();
                MediaManager.getInstance().startVideo(mCamera, mCameraGLView.getHolder(), mCameraSize, mRotation, filePath);
                mCurrentTime = System.currentTimeMillis();
                handleView(0);
                isRecording = true;
                startTimer();
                break;
            }
            case R.id.iv_play: {
                Intent intent = new Intent();
                intent.setClassName("com.ssdy.education.mobile.student", "com.ssdy.education.mobile.student.ui.activity.video.PlayerActivity");
                startActivity(intent);
                EventBus.getDefault().postSticky(new MediaEvent(filePath, 0, "", "'", "0", "0"));
//                EventBus.getDefault().postSticky(new MediaEvent(filePath,1,"", "", 0,false));
//                EventBus.getDefault().postSticky(new MediaEvent(filePath,2,"", "", 0,false));
//                EventBus.getDefault().postSticky(new MediaEvent(filePath,3,"", "干的漂亮", 50,false));
                isPlaying = true;
                break;
            }
        }
    }

    /**
     * 控制控件  0代表 正在录像    1.代表暂停界面   2.默认界面
     *
     * @param state
     */
    private void handleView(int state) {
        switch (state) {
            case 0: {
                //开始
                mRecordButton.setImageResource(R.drawable.selector_video_starting);
                mRecordButton.setVisibility(View.VISIBLE);
                useButton.setVisibility(View.GONE);
                ivPlay.setVisibility(View.GONE);
                customCameraShow.setVisibility(View.GONE);
                mReStartButton.setVisibility(View.GONE);
                break;
            }
            case 1: {
                //停止
                mRecordButton.setVisibility(View.GONE);
                useButton.setVisibility(View.VISIBLE);
                ivPlay.setVisibility(View.VISIBLE);
                customCameraShow.setVisibility(View.VISIBLE);
                mReStartButton.setVisibility(View.VISIBLE);
                break;
            }
            case 2: {
                //开始s
                mRecordButton.setImageResource(R.drawable.selector_video_start);
                mRecordButton.setVisibility(View.VISIBLE);
                useButton.setVisibility(View.GONE);
                ivPlay.setVisibility(View.GONE);
                customCameraShow.setVisibility(View.GONE);
                mReStartButton.setVisibility(View.GONE);
                break;
            }
        }
    }


    /**
     * @param drawId
     */
    public static void handleImage(final Context context, final ImageView ivRecord, final int drawId) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_play_in);
        final Animation animation2 = AnimationUtils.loadAnimation(context, R.anim.anim_play_out);
        ivRecord.startAnimation(animation);
        ivRecord.setClickable(false);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (ivRecord != null) {
                    ivRecord.startAnimation(animation2);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (ivRecord != null) {
                    ivRecord.setClickable(true);
                    ivRecord.setImageDrawable(context.getResources().getDrawable(drawId));
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    /**
     * 获取缩略图
     *
     * @param videoPath
     * @throws Exception
     */
    private void getBitmapsFromVideo(String videoPath) throws Exception {
        if (TextUtils.isEmpty(videoPath)) {
            return;
        }
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoPath);
        String rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        Bitmap bitmap = retriever.getFrameAtTime(1000 * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        customCameraShow.setImageBitmap(bitmap);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        CameraManager.getInstance().setPreview(holder);
        CameraManager.getInstance().start();
        isPlaying = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (!isPlaying) {
            handleView(2);
        }
        try {
            isRecording = false;
            MediaManager.getInstance().stopVideo();
            CameraManager.getInstance().stop();
            stopTimer();
            if (tvTimerTxt != null) {
                tvTimerTxt.setText("1:00");
            }
        } catch (Exception e) {
        }
    }
}




