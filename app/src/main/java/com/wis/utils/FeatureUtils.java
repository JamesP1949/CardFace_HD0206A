package com.wis.utils;

import android.graphics.Bitmap;

import com.socks.library.KLog;
import com.wis.face.WisMobile;
import com.wis.face.WisUtil;

/**
 * Created by JamesP949 on 2017/3/29.
 * Function:人脸特征工具类
 */

public class FeatureUtils {

    /**
     * 提取图片中的人脸特征
     *
     * @param bitmap
     * @return map元素key： 0--不包含人脸 1--包含人脸， value：人脸特征
     */
    public static float[] extractFeature(WisMobile wisMobile, Bitmap bitmap) {
        //get rgb data buffer from bitmap
        byte[] rgbData = WisUtil.getRGBByBitmap(bitmap);
        //detect face return face rect(x,y,width,height) in picture
        KLog.e("bitmap_width:" + bitmap.getWidth() + ", height:" + bitmap.getHeight());
        int[] ret = wisMobile.detectFace(rgbData, bitmap
                .getWidth(), bitmap.getHeight(), bitmap.getWidth() * 3);
        KLog.i("wisMobile", "detectFace size=" + ret[0] + ",rect(x,y,width,height) = " +
                ret[1] + "," + ret[2] + "," + ret[3] + "," + ret[4]);
        if (ret[0] > 0) {
            int faceRect[] = {ret[1], ret[2], ret[3], ret[4]};
            float[] fea = wisMobile.extractFeature(rgbData,
                    bitmap.getWidth(), bitmap.getHeight(), bitmap.getWidth() * 3, faceRect);
            KLog.i("wisMobile", "extractFeature " + fea);
            return fea;
        }
        return new float[0];
    }


    /**
     * 提取图片中的人脸特征
     *
     * @param wisMobile
     * @param bitmap
     * @return 数组 index--0: 人脸在图片中的坐标信息 index--1：图片中的人脸特征值
     */
    public static String[] extractFeature_(WisMobile wisMobile, Bitmap bitmap) {
        if (bitmap == null) {
            KLog.e("----------图片为空");
            return new String[]{"", ""};
        }
        String[] result = new String[2];
        //get rgb data buffer from bitmap
        byte[] rgbData = WisUtil.getRGBByBitmap(bitmap);
        //detect face return face rect(x,y,width,height) in picture
        KLog.i("bitmap_width:" + bitmap.getWidth() + ", height:" + bitmap.getHeight());
        int[] ret = wisMobile.detectFace(rgbData, bitmap
                .getWidth(), bitmap.getHeight(), bitmap.getWidth() * 3);
        KLog.i("wisMobile", "detectFace size=" + ret[0] + ",rect(x,y,width,height) = " +
                ret[1] + "," + ret[2] + "," + ret[3] + "," + ret[4]);
        StringBuffer rectSb = new StringBuffer();
        StringBuffer feaSb = new StringBuffer();
        // 判断图片中是否包含人脸 包含则提取信息
        if (ret[0] > 0) {
            int ret_x = ret[1];
            int ret_y = ret[2];
            int ret_width = ret[3];
            int ret_height = ret[4];
            rectSb.append(ret_x + ",").append(ret_y + ",").append(ret_width + ",").append
                    (ret_height);
            int faceRect[] = {ret_x, ret_y, ret_width, ret_height};
            float[] fea = wisMobile.extractFeature(rgbData,
                    bitmap.getWidth(), bitmap.getHeight(), bitmap.getWidth() * 3, faceRect);
            KLog.i("wisMobile", "extractFeature " + fea);
            //特征数组转化为String
            for (int i = 0; i < fea.length; i++) {
                if (i == fea.length - 1) {
                    feaSb.append(fea[i]);
                } else {
                    feaSb.append(fea[i] + ",");
                }
            }
        }
        result[0] = rectSb.toString();
        result[1] = feaSb.toString();
        return result;
    }

    public static float compare2IdCard(WisMobile wisMobile, float[] compareFea, float[] cardFea) {
        return wisMobile.compare2Feature(compareFea, cardFea) * 100;
    }
}
