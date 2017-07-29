package com.wufan.handler;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    ImageView threadIv;
    ImageView runnableIv;
    SendThread sendThread;
    PostRunnable postRunnable;
    private final MyHandler handler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        threadIv = (ImageView) findViewById(R.id.thread_iv);
        runnableIv = (ImageView) findViewById(R.id.runnable_iv);

        sendThread = new SendThread(handler);
        sendThread.start();

        postRunnable = new PostRunnable(handler);
        postRunnable.setRefreshUI(new PostRunnable.RefreshUI() {
            @Override
            public void setImage(byte[] data) {
                runnableIv.setImageBitmap(getBitmap(data));
            }
        });
        new Thread(postRunnable).start();


    }

    /**
     * 为避免handler造成的内存泄漏
     * ①使用静态的handler，对外部类不保持对象的引用
     * ②但Handler需要与Activity通信，所以需要增加一个对Activity的弱引用
     */
    private static class MyHandler extends Handler {
        private final WeakReference<Activity> mActivityReference;

        MyHandler(Activity activity) {
            this.mActivityReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = (MainActivity) mActivityReference.get();  //获取弱引用队列中的activity
            switch (msg.what) {    //获取消息，更新UI
                case 1:
                    byte[] data = (byte[]) msg.obj;
                    activity.threadIv.setImageBitmap(activity.getBitmap(data));
                    break;
            }
        }
    }

    private Bitmap getBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //避免activity销毁时，messageQueue中的消息未处理完；故此时应把对应的message给清除出队列
        handler.removeCallbacks(postRunnable);   //清除runnable对应的message
        //handler.removeMessage(what)  清除what对应的message
    }
}
