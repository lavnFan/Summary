package com.aliyun.ushell;

import android.content.Context;
import android.content.pm.ProviderInfo;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Created by wufan on 2016/7/4.
 */
public class MyLinearGradient extends LinearLayout {

    private LinearGradient linearGradient;
    private Paint paint;
    private int width;
    private int height;
    private int start = 0XFFFF8080;
    private int end = 0XFF8080FF;

    public MyLinearGradient(Context context) {
        super(context);
        getDisplay(context);
        setWillNotDraw(false);
    }

    public MyLinearGradient(Context context, AttributeSet attrs) {
        super(context, attrs);
        getDisplay(context);
        setWillNotDraw(false);
    }

    public MyLinearGradient(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getDisplay(context);
        setWillNotDraw(false);
    }

    private void getDisplay(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (paint == null) {
            paint = new Paint();
        }
        if (linearGradient != null) {
//            linearGradient = new LinearGradient(0, 0, width, height, start, end, Shader.TileMode.CLAMP);
            paint.setShader(linearGradient);
            canvas.drawRect(0, 0, width, height, paint);
        }
    }

    public void setGradient(int start, int end) {
        Log.i("Linear", start + " " + end);
        this.start = start;
        this.end = end;
        linearGradient = new LinearGradient(0, 0, 0, height, start, end, Shader.TileMode.CLAMP);
        invalidate();
    }
}
