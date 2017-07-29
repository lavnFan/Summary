package com.wufan.handler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by wufan on 2016/7/4.
 */
/**
 * 实现runnable接口，通过post（Runnable）通信，并通过给定的回调接口通知Activity更新
 */
public class PostRunnable implements Runnable {

    private Handler handler;
    private RefreshUI refreshUI;
    byte[] data = null;

    public PostRunnable(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        /**
         * 耗时操作
         */
        final Bitmap bitmap = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://i3.17173cdn.com/2fhnvk/YWxqaGBf/cms3/FNsPLfbkmwgBgpl.jpg");
        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                data = EntityUtils.toByteArray(httpResponse.getEntity());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //返回结果给UI线程
        handler.post(new Runnable() {
            @Override
            public void run() {
                refreshUI.setImage(data);
            }
        });
    }

    public interface RefreshUI {
        public void setImage(byte[] data);
    }

    public void setRefreshUI(RefreshUI refreshUI) {
        this.refreshUI = refreshUI;
    }
}
