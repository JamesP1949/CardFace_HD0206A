package com.common.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

import com.socks.library.KLog;

/**
 * Created by JamesP949 on 2017/3/6.
 * Function:标记人脸的相框
 */
public class DrawImageView_ extends AppCompatImageView {
    private Path mPath;
    private PathEffect mPathEffect;
    private int left, top, right, bottom = 0;
    private static float lineWidth = 30;

    public DrawImageView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPathEffect = new CornerPathEffect(10);
        mPath = new Path();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(150);
                        postInvalidate();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private boolean isChanged;

    public void setParam(int left, int top, int width, int height) {
        this.left = left;
        this.top = top;
        this.right = left + width;
        this.bottom = top + height;
        isChanged = true;
        KLog.e("setParam方法被调用：" + left + ", " + top + ", " + width + ", " + height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!mPath.isEmpty()) {
            Log.e("Path", "mPath不为空");
            // rewind当前path，清除掉所有直线，曲线，但是保留它内部的数据结构，以便更好的重新使用
            mPath.rewind();
        }

        if (isChanged) {
            canvas.drawColor(Color.TRANSPARENT);
            Paint mPaint = new Paint();
            // 清屏 防止重影
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawPaint(mPaint);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

            mPaint.setAntiAlias(true); // 反锯齿
            mPaint.setColor(Color.GREEN);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(3.5f);//设置线宽
            mPaint.setPathEffect(mPathEffect);
            /*   =======开始绘制相框=============*/
            mPath.moveTo(left + 10, top);
            mPath.lineTo(left + lineWidth, top);

            mPath.rMoveTo(right - left - 2 * lineWidth, 0);
            mPath.lineTo(right, top);
            mPath.rLineTo(0, lineWidth);
            // rLine后点的坐标为 right, top + lineWidth
            mPath.rMoveTo(0, bottom - top - 2 * lineWidth);
            // rMove后点的坐标为 right, bottom - lineWidth
            mPath.lineTo(right, bottom);
            mPath.rLineTo(-lineWidth, 0);
            // rLine后点的坐标为 right - lineWidth, bottom
            mPath.rMoveTo(-(right - left - 2 * lineWidth), 0);
            // rMove后点坐标 left + lineWidth, bottom
            mPath.lineTo(left, bottom);
            // 点坐标 left, bottom
            mPath.rLineTo(0, -lineWidth);
            // rLine后点坐标 left, bottom - lineWidth
            mPath.rMoveTo(0, -(bottom - top - 2 * lineWidth));
            // rMove后点坐标 left, top + lineWidth
            mPath.lineTo(left, top);
            // 多画一遍为了使左上角的现况闭合并有圆角
            mPath.rLineTo(lineWidth, 0);
//            mPath.close(); 使用close闭合没有圆角
            canvas.drawPath(mPath, mPaint);
            isChanged = false;
        } else
            return;
             /*   =======结束绘制相框=============*/
        Log.i("DrawImageView", "drawRect(left, top, right, bottom) = " + left + "," + top + "," +
                right + "," + bottom);
    }

}
