package com.wis.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.common.utils.StringUtils;

/**
 * Created by JamesP949 on 2017/2/17.
 * Function:
 */

public class UserConfig {
    private static final String UserFile = "userFile";
    private static UserConfig instance = null;
    private Context context;
    private SharedPreferences sp;

    private static final String NAME = "username";
    private static final String SEX = "sex";
    private static final String NATION = "nation";
    private static final String CREATE_TIME = "createTime";
    private static final String BIRTHDAY = "birthday";
    private static final String ADDRESS = "address";
    private static final String ADDRESS_LATEST = "latest_address";
    private static final String ID_NUM = "idNum";
    private static final String OFFICE = "office";
    private static final String VALID_DATE = "validDate"; // 有效期
    private static final String IMAGE_PATH = "imagePath";
    private static final String FINGER_PRINT = "finger_print";  // 指纹信息
    private static final String FACE_FEATURE = "face_feature";  // 身份证照片的人脸特征

    private UserConfig(Context context) {
        sp = context.getSharedPreferences(UserFile, Context.MODE_PRIVATE);
    }

    public synchronized static UserConfig getInstance(Context context) {
        if (instance == null) {
            synchronized (UserConfig.class) {
                if (instance == null) {
                    instance = new UserConfig(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public void setName(String name) {
        sp.edit().putString(NAME, name).apply();
    }

    public String getName() {
        return sp.getString(NAME, "");
    }

    public void setSex(String sex) {
        sp.edit().putString(SEX, sex).apply();
    }

    public String getSex() {
        return sp.getString(SEX, "");
    }

    public void setNation(String nation) {
        sp.edit().putString(NATION, nation).apply();
    }

    public String getNation() {
        return sp.getString(NATION, "");
    }

    public void setCreateTime(String createTime) {
        sp.edit().putString(CREATE_TIME, createTime).apply();
    }

    public String getCreateTime() {
        return sp.getString(CREATE_TIME, "");
    }

    public void setBirthday(String birthday) {
        sp.edit().putString(BIRTHDAY, birthday).apply();
    }

    public String getBirthday() {
        return sp.getString(BIRTHDAY, "");
    }

    public void setAddress(String address) {
        sp.edit().putString(ADDRESS, address).apply();
    }

    public String getAddress() {
        return sp.getString(ADDRESS, "");
    }

    public void setAddressLatest(String addressLatest) {
        sp.edit().putString(ADDRESS, addressLatest).apply();
    }

    public String getAddressLatest() {
        return sp.getString(ADDRESS_LATEST, "");
    }

    public void setIdNum(String idNum) {
        sp.edit().putString(ID_NUM, idNum).apply();
    }

    public String getIdNum() {
        return sp.getString(ID_NUM, "");
    }

    public void setOffice(String office) {
        sp.edit().putString(OFFICE, office).apply();
    }

    public String getOffice() {
        return sp.getString(OFFICE, "");
    }

    public void setValidDate(String validDate) {
        sp.edit().putString(VALID_DATE, validDate).apply();
    }

    public String getValidDate() {
        return sp.getString(VALID_DATE, "");
    }

    public void setImagePath(String imagePath) {
        sp.edit().putString(IMAGE_PATH, imagePath).apply();
    }

    public String getImagePath() {
        return sp.getString(IMAGE_PATH, "");
    }

    public void setFingerPrint(byte[] data) {
        sp.edit().putString(FINGER_PRINT, data.toString()).apply();
    }

    public byte[] getFingerPrint() {
        return sp.getString(FINGER_PRINT, "").getBytes();
    }

    public void setFaceFeature(float[] data) {
        sp.edit().putString(FACE_FEATURE, StringUtils.floatArr2String(data)).apply();
    }

    public float[] getFaceFeature() {
        return StringUtils.split2FloatArr(sp.getString(FACE_FEATURE, ""));
    }

    public void clearUserIno() {
        setName("");
        setSex("");
        setNation("");
        setBirthday("");
        setAddress("");
        setAddressLatest("");
        setIdNum("");
        setOffice("");
        setValidDate("");
        setImagePath("");
        setFingerPrint(new byte[0]);
        setFaceFeature(new float[0]);
//        setCreateTime("");
    }
}
