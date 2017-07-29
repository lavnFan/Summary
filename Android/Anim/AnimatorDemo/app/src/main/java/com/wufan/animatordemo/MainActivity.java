package com.wufan.animatordemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.frame_btn, R.id.tween_btn, R.id.object_btn})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            //帧动画：在一定时间内快速切换不同内容的图片，造成动画播放的效果
            case R.id.frame_btn:
                intent.setClass(this,FrameActivity.class);
                break;
            case R.id.tween_btn:
                intent.setClass(this,TweenActivity.class);
                break;
            case R.id.object_btn:
                intent.setClass(this,ObjectActivity.class);
                break;
        }
        startActivity(intent);
    }
}
