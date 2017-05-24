package com.common.utils;

import android.text.TextUtils;

/**
 * Created by JamesP949 on 2017/3/29.
 * Function:字符串操作工具类
 */

public class StringUtils {
    // 字符串拆分转换为float数组
    public static float[] split2FloatArr(String source) {
        String[] array = TextUtils.split(source, ",");
        float[] floats = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            floats[i] = Float.parseFloat(array[i]);
        }
        return floats;
    }

    // float数组转化为String字符串
    public static String floatArr2String(float[] source) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < source.length; i++) {
            if (i == source.length - 1) {
                stringBuffer.append(source[i]);
            } else {
                stringBuffer.append(source[i]).append(",");
            }
        }
        return stringBuffer.toString();
    }

    // 字符串拆分转换为int数组
    public static int[] split2IntArr(String source) {
        String[] array = TextUtils.split(source, ",");
        int[] ints = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            ints[i] = Integer.parseInt(array[i]);
        }
        return ints;
    }

    // float数组转化为String字符串
    public static String intArr2String(int[] source) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < source.length; i++) {
            if (i == source.length - 1) {
                stringBuffer.append(source[i]);
            } else {
                stringBuffer.append(source[i]).append(",");
            }
        }
        return stringBuffer.toString();
    }
}
