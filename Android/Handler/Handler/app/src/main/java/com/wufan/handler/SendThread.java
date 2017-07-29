package com.wufan.handler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

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
 * 继承Thread，通过handler的sendMessage通信的实例
 */
public class SendThread extends Thread {

    private Handler handler;

    public SendThread(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        super.run();
        /**
         * 耗时操作
         */
        byte[]data=null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("https://d36lyudx79hk0a.cloudfront.net/p0/descr/pc27/3095587d8c4560d8.png");
        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpGet);
            if(httpResponse.getStatusLine().getStatusCode()==200){
                data= EntityUtils.toByteArray(httpResponse.getEntity());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //返回结果给UI线程
        doTask(data);
    }

    /**
     * 通过handler返回消息
     * @param data
     */
    private void doTask(byte[] data) {
        Message msg =Message.obtain();  //从全局池中返回一个message实例，避免多次创建message（如new Message）
        msg.obj = data;
        msg.what=1;   //标志消息的标志
        handler.sendMessage(msg);
    }
}
