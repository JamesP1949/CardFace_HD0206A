package com.wis.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;


/**
 * Created by JamesP949 on 2017/4/27.
 * Function: 比对信息实体
 */
@Entity
public class Person {
    @Id(autoincrement = true)
    private Long id;
    private String name;
    private String sex;
    private String nation;
    private String cardId; // 身份证号
    private String address;
    private Long detectTime;
    private String idCardPhotoPath;
    private String detectPhotoPath;
    @Generated(hash = 617216170)
    public Person(Long id, String name, String sex, String nation, String cardId,
            String address, Long detectTime, String idCardPhotoPath,
            String detectPhotoPath) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.nation = nation;
        this.cardId = cardId;
        this.address = address;
        this.detectTime = detectTime;
        this.idCardPhotoPath = idCardPhotoPath;
        this.detectPhotoPath = detectPhotoPath;
    }
    @Generated(hash = 1024547259)
    public Person() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSex() {
        return this.sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }
    public String getNation() {
        return this.nation;
    }
    public void setNation(String nation) {
        this.nation = nation;
    }
    public String getAddress() {
        return this.address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public Long getDetectTime() {
        return this.detectTime;
    }
    public void setDetectTime(Long detectTime) {
        this.detectTime = detectTime;
    }
    public String getIdCardPhotoPath() {
        return this.idCardPhotoPath;
    }
    public void setIdCardPhotoPath(String idCardPhotoPath) {
        this.idCardPhotoPath = idCardPhotoPath;
    }
    public String getDetectPhotoPath() {
        return this.detectPhotoPath;
    }
    public void setDetectPhotoPath(String detectPhotoPath) {
        this.detectPhotoPath = detectPhotoPath;
    }
    public String getCardId() {
        return this.cardId;
    }
    public void setCardId(String cardId) {
        this.cardId = cardId;
    }
}
