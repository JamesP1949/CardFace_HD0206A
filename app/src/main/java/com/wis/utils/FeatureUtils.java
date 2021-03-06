package com.wis.utils;

import android.graphics.Bitmap;

import com.socks.library.KLog;
import com.wis.application.App;
import com.wis.face.WisUtil;

import java.util.HashMap;
import java.util.Map;

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
    public static float[] extractFeature(Bitmap bitmap) {
        //get rgb data buffer from bitmap
        byte[] rgbData = WisUtil.getRGBByBitmap(bitmap);
        //detect face return face rect(x,y,width,height) in picture
        KLog.e("bitmap_width:" + bitmap.getWidth() + ", height:" + bitmap.getHeight());
        int[] ret = App.getInstance().getWisMobile().detectFace(rgbData, bitmap
                .getWidth(), bitmap.getHeight(), bitmap.getWidth() * 3);
        KLog.i("wisMobile", "detectFace size=" + ret[0] + ",rect(x,y,width,height) = " +
                ret[1] + "," + ret[2] + "," + ret[3] + "," + ret[4]);
        if (ret[0] > 0) {
            int faceRect[] = {ret[1], ret[2], ret[3], ret[4]};
            float[] fea = App.getInstance().getWisMobile().extractFeature(rgbData,
                    bitmap.getWidth(), bitmap.getHeight(), bitmap.getWidth() * 3, faceRect);
            KLog.i("wisMobile", "extractFeature " + fea);
            return fea;
        }
        return new float[0];
    }


    /**
     * 提取图片中的人脸特征
     *
     * @param bitmap
     * @return 数组 index--0: 人脸在图片中的坐标信息 index--1：图片中的人脸特征值
     */
    public static String[] extractFeature_(Bitmap bitmap) {
        if (bitmap == null) {
            KLog.e("----------图片为空");
            return new String[]{"", ""};
        }
        String[] result = new String[2];
        //get rgb data buffer from bitmap
        byte[] rgbData = WisUtil.getRGBByBitmap(bitmap);
        //detect face return face rect(x,y,width,height) in picture
        KLog.i("bitmap_width:" + bitmap.getWidth() + ", height:" + bitmap.getHeight());
        int[] ret = App.getInstance().getWisMobile().detectFace(rgbData, bitmap
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
            float[] fea = App.getInstance().getWisMobile().extractFeature(rgbData,
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

    /**
     * 和数据库中的人脸进行比对
     *
     * @param personList
     * @param feature
     * @return 集合 index--0: 可以匹配成功的最高分 index--1：匹配度最高的人物信息
     *//*
    public static List<Object> matchWithDbData(List<Person> personList, float[] feature) {
        List<Object> objects = new ArrayList<>();
        float result = 0;
        Person p = new Person();
        for (int i = 0; i < personList.size(); i++) {
            Person person = personList.get(i);
            float score = WisApplication.instance.getWisMobile().compare2Feature(feature,
                    StringUtils.split2Float(person.getFeature())) * 100;
            if (score >= 50 && score > result) {
                result = score;
                p.setName(person.getName());
                p.set_id(person.get_id());
                p.setImage(person.getImage());
            }
        }
        if (result >= 50) {
            objects.add(result);
            objects.add(p);
        }

        return objects;

    }*/
    public static float compare2IdCard(float[] compareFea, float[] cardFea) {
        return App.getInstance().getWisMobile().compare2Feature(compareFea, cardFea) * 100;
    }
}
