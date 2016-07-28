package com.aliyun.ushell;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dominantcolors.DominantColor;
import com.dominantcolors.DominantColors;
import com.utils.ImageUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.show_blur_iv)
    ImageView showBlurIv;
    @BindView(R.id.camera_preview)
    FrameLayout preview;
    @BindView(R.id.my_linear_gradient)
    MyLinearGradient myLinearGradient;
    @BindView(R.id.show_view_my_linear_gradient)
    MyLinearGradient viewLinearGradient;
    @BindView(R.id.show_blur_bg_iv)
    ImageView mShowBlurbgIv;
    @BindView(R.id.show_blur_bg_iv_2)
    ImageView mShowBlurbgIv2;

    FrostedGlassUtil frostedGlassUtil;
    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler mHandler = new Handler();
    private ExecutorService singleExecutor;
    private boolean isCapture = true;

    private AnimationDrawable frameAnim = new AnimationDrawable();
    Bitmap roadBmp1, roadBmp2, roadBmp3, roadBmp4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        frostedGlassUtil = FrostedGlassUtil.getInstance();

        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        setCameraParams(mCamera);
        mPreview = new CameraPreview(this, mCamera);
        preview.addView(mPreview);
        TimerThread timerThread = new TimerThread();
        timerThread.start();
        singleExecutor = Executors.newSingleThreadExecutor();

        decodeBitmapFromeResource();
    }

    private void decodeBitmapFromeResource() {
        roadBmp1 = BitmapFactory.decodeResource(getResources(), R.drawable.dynamic_road_running_04);
        roadBmp2 = BitmapFactory.decodeResource(getResources(), R.drawable.dynamic_road_running_03);
        roadBmp3 = BitmapFactory.decodeResource(getResources(), R.drawable.dynamic_road_running_02);
        roadBmp4 = BitmapFactory.decodeResource(getResources(), R.drawable.dynamic_road_running_01);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            mCamera = getCameraInstance();
            setCameraParams(mCamera);
            mPreview.setmCamera(mCamera);
            mCamera.startPreview();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        frameAnim.stop();
        frameAnim = null;
        singleExecutor.shutdown();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    // 设置摄像头参数
    protected void setCameraParams(Camera camera) {
        camera.setDisplayOrientation(90);
        Camera.Parameters params = camera.getParameters();
        params.setRotation(90);
        camera.setParameters(params);
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
            Log.i("TAG", "success!");
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Log.i("TAG", "failed!");
        }
        return c; // returns null if camera is unavailable
    }

    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] bytes, Camera camera) {
            if (isCapture) {      //在间隔之间获取预览相机中的图片
                isCapture = false;
                //按间隔取某个数据帧的图片
                //传进来的data，默认是YUV420SP的，需转换为图片的字节流数据
                Camera.Size size = mCamera.getParameters().getPreviewSize();
                YuvImage image = new YuvImage(bytes, ImageFormat.NV21, size.width, size.height, null);
                if (image != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compressToJpeg(new Rect(0, 0, size.width, size.height),  //(left, top, right bottom)
                            80, stream);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                    //进行高斯模糊，需先旋转90度，截取流里的图片是旋转90度的
                    singleExecutor.execute(new GlassThread(ImageUtil.rotate(bitmap, 90)));
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    class TimerThread extends Thread {

        @Override
        public void run() {
            while (true) {
                try {
                    sleep(6000);
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            mCamera.setPreviewCallback(mPreviewCallback);
                        }
                    };
                    mHandler.post(runnable);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class GlassThread extends Thread {

        Bitmap bitmap;
        Bitmap glassBitmap;

        public GlassThread(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        @Override
        public void run() {
//            long start_time = System.currentTimeMillis();
            //高斯模糊
            glassBitmap = frostedGlassUtil.convertToBlur(ImageUtil.comp(bitmap), 30);
            long center_time = System.currentTimeMillis();
            //提取主色调
            final DominantColor[] colors = DominantColors.getDominantColors(glassBitmap, 1);
//            long end_time = System.currentTimeMillis();
//            long glass_time = center_time-start_time;
//            long dominant_time = end_time-center_time;
//            Log.i("Thread","glass:"+glass_time+" dominant:"+dominant_time);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    myLinearGradient.setGradient(colors[0].color, getEndColor(colors[0].color, 0.5));
                    viewLinearGradient.setGradient(colors[0].color, getEndColor(colors[0].color, 0.5));

                    mShowBlurbgIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    mShowBlurbgIv2.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    setFrameAnim(glassBitmap);
//                    showBlurIv.setImageBitmap(roadBmp1);
                    mShowBlurbgIv2.setImageBitmap(glassBitmap);
                    mShowBlurbgIv.setImageBitmap(glassBitmap);

                    ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(mShowBlurbgIv, "alpha", 1.0f, 0.0f);
                    ObjectAnimator appearAnim = ObjectAnimator.ofFloat(mShowBlurbgIv2, "alpha", 0.0f, 1.0f);
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.setDuration(500);
                    animatorSet.setInterpolator(new LinearInterpolator()); //线性插值，均匀变化
                    animatorSet.playTogether(fadeAnim,appearAnim);
                    animatorSet.start();


//                    ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(mShowBlurbgIv, "alpha", 1.0f, 0.0f);
//                    ObjectAnimator appearAnim = ObjectAnimator.ofFloat(showBlurIv,"alpha",0.0f,1.0f);
//                    AnimatorSet animatorSet = new AnimatorSet();
//                    animatorSet.setDuration(500);
//                    animatorSet.setInterpolator(new LinearInterpolator()); //线性插值，均匀变化
//                    animatorSet.play(fadeAnim).before(appearAnim);
//                    animatorSet.start();
//                    animatorSet.addListener(new Animator.AnimatorListener() {
//                        @Override
//                        public void onAnimationStart(Animator animator) {
//
//                        }
//
//                        @Override
//                        public void onAnimationEnd(Animator animator) {
//                            //1、把高斯模糊的图片与道路图片进行叠加
//                            //2、4张叠加的图片进行帧动画播放
//                            if(frameAnim.isRunning()){
//                                frameAnim.stop();
//                                frameAnim = null;
//                                frameAnim = new AnimationDrawable();
//                            }
//                            setFrameAnim(glassBitmap);
//                        }
//
//                        @Override
//                        public void onAnimationCancel(Animator animator) {
//
//                        }
//
//                        @Override
//                        public void onAnimationRepeat(Animator animator) {
//
//                        }
//                    });

//                    showBlurIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                    showBlurIv.setImageDrawable(getLayerBitmaps(glassBitmap, roadBmp));
//                    showBlurIv.setImageBitmap(getOverlay(roadBmp,glassBitmap ));
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                }
            };
            mHandler.post(runnable);
            try {
                sleep(6000);
                isCapture = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private int getEndColor(int startColor, double percentage) {
        int sR = Color.red(startColor);
        int sG = Color.green(startColor);
        int sB = Color.blue(startColor);
        int wR = Color.red(Color.WHITE);
        int wG = Color.green(Color.WHITE);
        int wB = Color.blue(Color.WHITE);
        int endColor = (sR + (int) ((sR - wR) * percentage)) / 2 + (sG + (int) ((sG - wG) * percentage)) / 2 + (sB + (int) ((sB - wB) * percentage)) / 2;
        return endColor;
    }

    private LayerDrawable getLayerBitmaps(Bitmap bitmap1, Bitmap bitmap2) {
        Drawable[] array = new Drawable[2];
        array[0] = new BitmapDrawable(bitmap1);
        array[1] = new BitmapDrawable(bitmap2);
        LayerDrawable la = new LayerDrawable(array);
        la.setLayerInset(0, 0, 0, 0, 0);
        la.setLayerInset(1, 0, 0, 0, 0);
        return la;
    }

    /**
     * @param bitmap：高斯模糊的图片 与道路图片进行叠加
     *                       再做成帧动画
     */
    private void setFrameAnim(Bitmap bitmap) {
        frameAnim.setOneShot(false);
        frameAnim.addFrame(getResources().getDrawable(R.drawable.dynamic_road_running_04), 100);
        frameAnim.addFrame(getResources().getDrawable(R.drawable.dynamic_road_running_03), 100);
        frameAnim.addFrame( getResources().getDrawable(R.drawable.dynamic_road_running_02),100);
        frameAnim.addFrame( getResources().getDrawable(R.drawable.dynamic_road_running_01), 100);
//        frameAnim.addFrame(getLayerBitmaps(bitmap,roadBmp1),100);
//        frameAnim.addFrame(getLayerBitmaps(bitmap,roadBmp2),100);
//        frameAnim.addFrame(getLayerBitmaps(bitmap,roadBmp3),100);
//        frameAnim.addFrame(getLayerBitmaps(bitmap,roadBmp4),100);
        showBlurIv.setImageDrawable(frameAnim);
        frameAnim.start();
    }

    private void startPlayFrameAnim() {
        frameAnim.start();
    }

    private void stopPlayFrameAnim() {
        frameAnim.stop();
    }


}