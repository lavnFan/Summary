package com.seu.touchevent;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by wufan on 2015/10/29.
 */
public class MyLinearLayout extends LinearLayout{

    public static final String TAG="ViewGroup2";

    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG,"ACTION_DOWN dispatchTouchEvent ");
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG,"ACTION_UP dispatchTouchEvent ");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG,"ACTION_MOVE dispatchTouchEvent ");
                break;
        }
//        return true;
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG,"ACTION_DOWN onInterceptTouchEvent ");
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG,"ACTION_UP onInterceptTouchEvent ");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG,"ACTION_MOVE onInterceptTouchEvent ");
                break;
        }
//        return true;
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG,"ACTION_DOWN onTouchEvent ");
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG,"ACTION_UP onTouchEvent ");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG,"ACTION_MOVE onTouchEvent ");
                break;
        }
//        return true;
        return super.onTouchEvent(event);
    }
}
