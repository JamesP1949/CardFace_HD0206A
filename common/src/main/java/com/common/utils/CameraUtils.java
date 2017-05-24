package com.common.utils;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;

import com.socks.library.KLog;

import java.util.List;

/**
 * Created by JamesP949 on 2017/2/27.
 * Function:适配手机支持的最佳屏幕尺寸
 */

public class CameraUtils {
    /**
     * 找到最佳预览尺寸
     *
     * @param context
     * @param camera
     * @param parameters
     * @param aspectRatio
     * @return
     */
    public static Camera.Size findBestPreviewSize(Context context, Camera camera, Camera
            .Parameters parameters, float aspectRatio) {
        /*Point screenResolution = new Point(DisplayUtils.getMetrics(context).widthPixels,
                DisplayUtils.getMetrics(context).heightPixels);*/
        float tmp = 0f;
        float mindiff = 100f;
//        float x_d_y = (float) screenResolution.x / (float) screenResolution.y;
//        KLog.e("x_d_y：" + x_d_y);
        Camera.Size best = null;
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        for (Camera.Size s : supportedPreviewSizes) {
            KLog.e("preview width:" + s.width + ", height:" + s.height);
            tmp = Math.abs(((float) s.width / (float) s.height) - aspectRatio);
            if (tmp < mindiff) {
                mindiff = tmp;
                best = s;
            }
        }
        return camera.new Size(best.width, best.height);
    }

    /**
     * 找到最佳的图片预览尺寸
     *
     * @param context
     * @param camera
     * @param parameters
     * @return
     */
    public static Camera.Size findBestPictureSize(Context context, Camera camera, Camera.Parameters
            parameters) {
        int diff = Integer.MIN_VALUE;
        List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
        int bestX = 0;
        int bestY = 0;


        for (Camera.Size pictureSize : pictureSizes) {
            int newX = pictureSize.width;
            int newY = pictureSize.height;

            Point screenResolution = new Point(DisplayUtils.getMetrics(context).widthPixels,
                    DisplayUtils.getMetrics(context).heightPixels);

            int newDiff = Math.abs(newX - screenResolution.x) + Math.abs(newY - screenResolution.y);
            if (newDiff == diff) {
                bestX = newX;
                bestY = newY;
                break;
            } else if (newDiff > diff) {
                if ((3 * newX) == (4 * newY)) {
                    bestX = newX;
                    bestY = newY;
                    diff = newDiff;
                }
            }
        }

        if (bestX > 0 && bestY > 0) {
            return camera.new Size(bestX, bestY);
        }
        return null;
    }
}
