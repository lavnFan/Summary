package com.wufan.animatordemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wufan on 2016/7/16.
 */
public class TweenActivity extends Activity {

    @BindView(R.id.tween_iv)
    ImageView tweenIv;
    Animation tweenAnimation;
    @BindView(R.id.tween_start_btn)
    Button startBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tween_layout);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.tween_start_btn)
    void startAnimation() {
        tweenAnimation = new AnimationUtils().loadAnimation(this, R.anim.tween_animation);
        tweenIv.startAnimation(tweenAnimation);
    }
}
