package com.wufan.animatordemo;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wufan on 2016/7/16.
 */
public class FrameActivity extends Activity {

    @BindView(R.id.frame_iv)
    ImageView mFrameIv;
    @BindView(R.id.frame_code_iv)
    ImageView mFrameCodeIv;
    @BindView(R.id.frame_code_btn)
    Button mFrameControlBtn;

    private AnimationDrawable frameDrawableAnim = null;
    private AnimationDrawable frameCodeAnim = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_layout);
        ButterKnife.bind(this);

        setFrameDrawableAnimation();
    }

    /**
     *  从XML中读取animation-list
     */
    private void setFrameDrawableAnimation() {
        frameDrawableAnim = (AnimationDrawable) getResources()
                .getDrawable(R.drawable.dynamic_road_running);
        mFrameIv.setImageDrawable(frameDrawableAnim);
        frameDrawableAnim.start();
    }

    /**
     * 代码控制，依次添加帧动画的每一帧图片
     */
    private void setFrameCodeAnimation() {
        frameCodeAnim = new AnimationDrawable();
        frameCodeAnim.setOneShot(false);  //true：动画只显示一次，这里设置为false，持续显示动画
        frameCodeAnim.addFrame(getResources().getDrawable(R.drawable.progress_1),200);
        frameCodeAnim.addFrame(getResources().getDrawable(R.drawable.progress_2),200);
        frameCodeAnim.addFrame(getResources().getDrawable(R.drawable.progress_3),200);
        frameCodeAnim.addFrame(getResources().getDrawable(R.drawable.progress_4),200);
        frameCodeAnim.addFrame(getResources().getDrawable(R.drawable.progress_5),200);
        frameCodeAnim.addFrame(getResources().getDrawable(R.drawable.progress_6),200);
        frameCodeAnim.addFrame(getResources().getDrawable(R.drawable.progress_7),200);
        frameCodeAnim.addFrame(getResources().getDrawable(R.drawable.progress_8),60);
        mFrameCodeIv.setImageDrawable(frameCodeAnim);
        frameCodeAnim.start();
    }

    @OnClick(R.id.frame_code_btn)
    public void onClick() {
        setFrameCodeAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        frameDrawableAnim.start();
        if(frameCodeAnim!=null){
            frameCodeAnim.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        frameDrawableAnim.stop();
        if(frameCodeAnim!=null){
            frameCodeAnim.stop();
        }
    }
}
