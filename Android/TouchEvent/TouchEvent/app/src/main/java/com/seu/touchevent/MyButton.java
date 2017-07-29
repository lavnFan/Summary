package com.seu.touchevent;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;

/**
 * Created by wufan on 2015/10/29.
 */
public class MyButton extends Button{
    public static final String TAG="View";
    public MyButton(Context context) {
        super(context);
    }

    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "ACTION_UP dispatchTouchEvent ");
                break;
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "ACTION_DOWN dispatchTouchEvent ");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "ACTION_MOVE dispatchTouchEvent ");
                break;
        }
//        return false;
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "ACTION_UP onTouchEvent ");
                break;
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "ACTION_DOWN onTouchEvent ");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "ACTION_MOVE onTouchEvent ");
                break;
        }
        return false;
//        return super.onTouchEvent(event);
    }

}
