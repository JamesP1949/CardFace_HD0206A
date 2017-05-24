package com.common.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.socks.library.KLog;

/**
 * Created by JamesP949 on 2017/2/27.
 * Function:获取手机屏幕相关
 */

public class DisplayUtils {
    public static DisplayMetrics getMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    public static float getScreenRatio(Context context) {
        DisplayMetrics metrics = getMetrics(context);
        KLog.e("屏幕分辨率：width：" + metrics.widthPixels + " * 高度：" + metrics.heightPixels + ", density:" + metrics.densityDpi);
        return metrics.widthPixels/metrics.heightPixels;
    }

    /**
     *  反射方式获得 耗费性能
     * 获取状态栏的高度
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int statusHeight = -1;
        try {
            Class clz = Class.forName("com.android.internal.R$dimen");
            Object object = clz.newInstance();
            int height = Integer.parseInt(clz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }


    public static int getStatusBarHeight_(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static float dp2px(Context context, int dp) {
        return dp2px(context.getResources().getDisplayMetrics(), dp);
    }

    public static float dp2px(DisplayMetrics displayMetrics, int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

    /**
     * 控件测量宽度
     * @param context
     * @param offset  每个控件的偏移量
     * @param count 控件的个数
     * @return
     */
    public static int getWidth(Context context, int offset, int count) {
        float screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        float totalOffset = dip2px(context, (count - 1) * offset);
        return (int) ((screenWidth - totalOffset) / count);
    }

    /**
     * dip2px(Context context, float dipValue)与此方法效果相同
     * @param context
     * @param dip
     * @return
     */
    public static float dip2px(Context context, int dip) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources()
                .getDisplayMetrics());
    }

    /**
     * 以控件宽度为准 根据比例测算控件高度
     * @param width
     * @param scale
     * @return
     */
    public static int getHeight(int width, float scale) {
        return (int) (width / scale);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        /*final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);*/
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) (dipValue * (metrics.densityDpi / 160f));

    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param context （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param context （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
