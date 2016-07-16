package com.wufan.animatordemo;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wufan on 2016/7/16.
 */
public class ObjectActivity extends Activity {

    @BindView(R.id.object_animation_view_01_iv)
    ImageView mAnimationView01Iv;
    @BindView(R.id.object_animation_view_02_iv)
    ImageView mAnimationView02Iv;

    private ImageView mCurView;
    private Drawable drawable = null;
    private boolean bDrawable_1 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.object_layout);
        ButterKnife.bind(this);
        drawable = getResources().getDrawable(R.drawable.cat);
    }

    @OnClick(R.id.object_animation_btn)
    void startAnimation() {
        setObjectAnimation();
    }

    private void setObjectAnimation() {
        setObjectAnimation();
        final ImageView newBgView = mCurView == mAnimationView01Iv ? mAnimationView02Iv : mAnimationView01Iv;
        changeDrawable();      //俩张图片轮询更替
        newBgView.setImageDrawable(drawable);

        ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(mCurView, "alpha", 1.0f, 0.0f);     //前一张图片淡出
        ObjectAnimator appearAnim = ObjectAnimator.ofFloat(newBgView, "alpha", 0.0f, 1.0f);   //后一张图片淡入
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(2000);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.playTogether(fadeAnim, appearAnim);
        animatorSet.start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mCurView = newBgView;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void changeDrawable() {
        if (bDrawable_1) {
            drawable = getResources().getDrawable(R.drawable.cat2);
            bDrawable_1 = false;
        } else {
            drawable = getResources().getDrawable(R.drawable.cat);
            bDrawable_1 = true;
        }
    }

}
