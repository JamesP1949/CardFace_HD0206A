package com.wis.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
@Deprecated
/**
 * 使用系统默认的SharedPreferences代替SettingConfig
 */
public class SettingConfig {
    private static SettingConfig instance = null;
    public static final String SettingFile = "settingFile";
    private Context context;
    private SharedPreferences sp;
    private String COMPARISON_THRESHOLD = "c_threshold";//比对阈值
    private String TIME_THRESHOLD = "t_threshold";//倒计时阈值
    private String READER_SDK_VERSION = "read_version";//读卡SDK版本
    private String MACHINE_SN = "machine_sn";//读卡设备的序列号
    private String MOBILE_SN = "moble_sn"; // 人脸比对SDK收集的设备序列号


    private SettingConfig(Context context) {
        sp = context.getSharedPreferences(SettingFile, Context.MODE_PRIVATE);
    }

    public synchronized static SettingConfig getInstance(Context context) {
        if (instance == null) {
            synchronized (SettingConfig.class) {
                if (instance == null) {
                    instance = new SettingConfig(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    // 用户设置比对阈值
    public void saveCThreshold(int threshold) {
        Editor ed = sp.edit();
        ed.putInt(COMPARISON_THRESHOLD, threshold);
        ed.commit();
    }

    // 默认比对阈值：80
    public int getCThreshold() {
        return sp.getInt(COMPARISON_THRESHOLD, 80);
    }

    // 用户设置动态比对倒计时阈值
    public void saveT_Threshold(int threshold) {
        Editor ed = sp.edit();
        ed.putInt(TIME_THRESHOLD, threshold);
        ed.commit();
    }

    // 默认动态比对阈值：10s
    public int getT_Threshold() {
        return sp.getInt(TIME_THRESHOLD, 10);
    }

    public String getREADER_SDK_VERSION() {
        return sp.getString(READER_SDK_VERSION, "");
    }

    public void setREADER_SDK_VERSION(String r_version) {
        Editor ed = sp.edit();
        ed.putString(READER_SDK_VERSION, r_version);
        ed.commit();
    }

    public String getMACHINE_SN() {
        return sp.getString(MACHINE_SN, "");
    }

    public void setMACHINE_SN(String m_sn) {
        Editor ed = sp.edit();
        ed.putString(MACHINE_SN, m_sn);
        ed.commit();
    }

    public String getMOBILE_SN() {
        return sp.getString(MOBILE_SN, "");
    }

    public void setMOBILE_SN(String mobile_sn) {
        Editor ed = sp.edit();
        ed.putString(MOBILE_SN, mobile_sn);
        ed.commit();
    }
}