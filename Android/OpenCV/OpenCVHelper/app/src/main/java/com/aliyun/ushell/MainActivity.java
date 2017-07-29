package com.aliyun.ushell;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.aliyun.ushell.utils.ImageUtil;
import com.aliyun.utils.OpenCVHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.camera_preview)
    FrameLayout preview;
    @BindView(R.id.result_corrlation_tv)
    TextView mCorrlationTv;
    @BindView(R.id.result_chi_square_tv)
    TextView mChiSquareTv;
    @BindView(R.id.result_intersection_tv)
    TextView mIntersectionTv;
    @BindView(R.id.result_bhattachryya_tv)
    TextView mBhattachryyaTv;
    @BindView(R.id.show_gauss_old_iv)
    ImageView mGaussOldIv;
    @BindView(R.id.show_gauss_new_iv)
    ImageView mGaussNewIv;
    @BindView(R.id.show_old_iv)
    ImageView mOldIv;
    @BindView(R.id.show_new_iv)
    ImageView mNewIv;

    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler mHandler = new Handler();
    private boolean isCapture = true;
    private Bitmap pictureBmp = null;
    private String old_image = "/old.jpg", new_image = "new.jpg", temp_image = new_image;
    private String prefix = "/sdcard/Boohee/";
    private double diff = 0;
    private int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        setCameraParams(mCamera);
        mPreview = new CameraPreview(this, mCamera);
        preview.addView(mPreview);
        TimerThread timerThread = new TimerThread();
        timerThread.start();
    }


    public static void saveImage(Bitmap bmp, String news) {
        File appDir = new File(Environment.getExternalStorageDirectory(), "Boohee");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = news;
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        mCamera.stopPreview();
        releaseCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_1:
                if (checked)
                    type = 0;
                // Pirates are the best
                break;
            case R.id.radio_2:
                if (checked)
                    type = 1;
                // Ninjas rule
                break;
            case R.id.radio_3:
                if (checked)
                    type = 2;
                // Ninjas rule
                break;
            case R.id.radio_4:
                if (checked)
                    type = 3;
                // Ninjas rule
                break;
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
                    pictureBmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //旋转90度，截取流里的图片是旋转90度的
                    pictureBmp = ImageUtil.rotate(pictureBmp, 90);
                    pictureBmp=ImageUtil.comp(pictureBmp);
                    saveImage(pictureBmp, new_image);
                    //和前一张图片比较相似度
                    for (int i = 0; i < 4; i++) {
                        diff = OpenCVHelper.compareImages(prefix + old_image, prefix + new_image, i);
                        updateText(diff, i);
                    }
                    long time_start = SystemClock.currentThreadTimeMillis();
                    diff = OpenCVHelper.compareImages(prefix + old_image, prefix + new_image, type);
                    long time_end = SystemClock.currentThreadTimeMillis();
                    Log.i("MainActivity", diff + " type:" + type +"cost time:"+(time_end-time_start));
                    updateUI(diff, type);
                }
            }
        }
    };

    class TimerThread extends Thread {

        @Override
        public void run() {
            while (true) {
                try {
                    sleep(2000);
                    isCapture = true;
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

    private void updateUI(double diff, int type) {
        switch (type) {
            case 0:
                mCorrlationTv.setText(String.valueOf(diff));
                if (diff < 0.3) {   //更换图片及其路径
                    mNewIv.setImageBitmap(pictureBmp);
                    mOldIv.setImageBitmap(ImageUtil.decodeSampledBitmapFromFile(prefix + old_image,100,100));
                    mGaussOldIv.setImageBitmap(ImageUtil.decodeSampledBitmapFromFile("/sdcard/Boohee/gauss_old.jpg",100,100));
                    mGaussNewIv.setImageBitmap(ImageUtil.decodeSampledBitmapFromFile("/sdcard/Boohee/gauss_new.jpg",100,100));

                    temp_image = old_image;
                    old_image = new_image;
                    new_image = temp_image;
                }
                break;
            case 3:
                mBhattachryyaTv.setText(String.valueOf(diff));
                if (diff > 0.7) {   //更换图片及其路径
                    mNewIv.setImageBitmap(pictureBmp);
                    temp_image = old_image;
                    old_image = new_image;
                    new_image = temp_image;
                }
                break;
            case 1:
                mChiSquareTv.setText(String.valueOf(diff));
                break;
            case 2:
                mIntersectionTv.setText(String.valueOf(diff));
                break;

        }
    }

    private void updateText(double diff, int type) {
        switch (type) {
            case 0:
                mCorrlationTv.setText(String.valueOf(diff));
                break;
            case 3:
                mBhattachryyaTv.setText(String.valueOf(diff));
                break;
            case 1:
                mChiSquareTv.setText(String.valueOf(diff));
                break;
            case 2:
                mIntersectionTv.setText(String.valueOf(diff));
                break;
        }
    }

}
