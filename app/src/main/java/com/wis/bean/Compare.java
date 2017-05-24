package com.wis.bean;

import android.graphics.Bitmap;

/**
 * Created by JamesP949 on 2017/5/3.
 * Function:
 */

public class Compare {
    private Bitmap mBitmap; // 比对原始图
    private Bitmap cropBitmap;  // 剪切的比对人脸图
    private Long aLong; // 时间计数
    private String faceRect; // 人脸坐标信息
    private String faceFeature; // 人脸特征值信息
    private float compareScore;
    private long compareTime; //比对成功时间
    private String cropPath; // 人脸图存储地址
    private String key; // 比对原始图在内存中的key
    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public Bitmap getCropBitmap() {
        return cropBitmap;
    }

    public void setCropBitmap(Bitmap cropBitmap) {
        this.cropBitmap = cropBitmap;
    }

    public Long getaLong() {
        return aLong;
    }

    public void setaLong(Long aLong) {
        this.aLong = aLong;
    }

    public String getFaceRect() {
        return faceRect;
    }

    public void setFaceRect(String faceRect) {
        this.faceRect = faceRect;
    }

    public String getFaceFeature() {
        return faceFeature;
    }

    public void setFaceFeature(String faceFeature) {
        this.faceFeature = faceFeature;
    }

    public float getCompareScore() {
        return compareScore;
    }

    public void setCompareScore(float compareScore) {
        this.compareScore = compareScore;
    }

    public long getCompareTime() {
        return compareTime;
    }

    public void setCompareTime(long compareTime) {
        this.compareTime = compareTime;
    }

    public String getCropPath() {
        return cropPath;
    }

    public void setCropPath(String cropPath) {
        this.cropPath = cropPath;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void clear() {
        if (mBitmap != null && !mBitmap.isRecycled())
            mBitmap.recycle();
        if (cropBitmap != null && !cropBitmap.isRecycled())
            cropBitmap.recycle();
        System.gc();
    }
}
