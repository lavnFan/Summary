package com.seu.touchevent;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * @author wufan
 * @date 2016/4/21
 */
public class MyLinearLayoutOne extends LinearLayout {
    public static final String TAG="ViewGroup1";

    public MyLinearLayoutOne(Context context) {
        super(context);
    }

    public MyLinearLayoutOne(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayoutOne(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "ACTION_DOWN dispatchTouchEvent ");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG,"ACTION_MOVE dispatchTouchEvent ");
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG,"ACTION_UP dispatchTouchEvent ");
                break;
        }
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
 //       return true;
        return super.onTouchEvent(event);
    }
}
