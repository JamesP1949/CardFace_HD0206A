package com.wis.face;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.socks.library.KLog;

import java.io.File;

/**
 * Created by wis on 16-7-26.
 */
public class WisMobile {

    static {
        System.loadLibrary("caffe");
        System.loadLibrary("wis_engine_jni");
    }

    /**
     * 两张人脸照片进行比对,返回相似度(0~1)之间, 如果大于0.5就代表很相似,如果超过0.9表示两个照片属于同一个人的概率极大
     *
     * @return
     */
    public float calculate2ImageSimilarity(String imgFile1, String imgFile2) {

            KLog.e("imgFile1--" + imgFile1);
            KLog.e("imgFile2--" + imgFile2);
        //请自行判断两个文件的存在性,sdk内部不做文件是否存在判断
        Bitmap bmp1 = BitmapFactory.decodeFile(imgFile1);
        Bitmap bmp2 = BitmapFactory.decodeFile(imgFile2);
        if (bmp1 == null) {
            KLog.e("身份证照片为null");
        }

        if (bmp2 == null) {
            KLog.e("比对照片为null");
        }
        float[] fea1 = detectFace(bmp1);
        if (fea1 == null) {
            KLog.e("fea1 == null");
            return 0;
        }
        float[] fea2 = detectFace(bmp2);
        if (fea2 == null) {
            KLog.e("fea2 == null");
            return 0;
        }
        float score = compare2Feature(fea1, fea2);
        Log.i("wisMobile", "score   " + score);
        return score;
    }

    public float[] detectFace(Bitmap bmp) {
        byte[] rgbData = WisUtil.getRGBByBitmap(bmp);
        //detect face return face rect(x,y,width,height) in picture
        long startTime = System.nanoTime();
        int[] ret = detectFace(rgbData, bmp.getWidth(), bmp.getHeight(), bmp.getWidth() * 3);
        long consumingTime = System.nanoTime() - startTime;
        Log.i("wisMobile", "detect time  " + consumingTime / 1000);
        Log.i("wisMobile", "detectFace num =" + ret[0] + ",rect(x,y,width,height) = " + ret[1] +
                "," + ret[2] + "," + ret[3] + "," + ret[4]);
        if (ret[0] < 1) {
            return null;
        }
        startTime = System.nanoTime();
        int faceRect[] = {ret[1], ret[2], ret[3], ret[4]};
        float[] fea = extractFeature(rgbData, bmp.getWidth(), bmp.getHeight(), bmp.getWidth() *
                3, faceRect);
        consumingTime = System.nanoTime() - startTime;
        Log.i("wisMobile", "extractFeature time  " + consumingTime / 1000);
        return fea;
    }


    /**
     * 获取android机器码（32位）
     *
     * @return
     */
    public native String getSerialNo();  // required

    /**
     * @param numThreads
     */
    public native void setNumThreads(int numThreads);

    public native int loadModel(String modelDir);  // required

    //Version2.0 下一版本开放,暂时不要使用
    public native float compare2Image(String imgFile1, String imgFile2);

    //face detect
    public native int[] detectFace(byte[] rgb32, int width, int height, int widthStep);

    //extract face feature
    public native float[] extractFeature(byte[] rgb32, int width, int height, int widthStep,
                                         int[] faceRect);

    //calculate two face feature similarity
    public native float compare2Feature(float[] fea1, float[] fea2);

}
