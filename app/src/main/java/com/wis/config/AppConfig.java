package com.wis.config;


import android.os.Environment;

import com.wis.application.App;

import java.io.File;

public class AppConfig {
    /**
     * 数据文件的根路径 目前定在外部存储卡中
     */
    public static final String BaseFilePath = App.getInstance().getExternalFilesDir(Environment
            .DIRECTORY_DOCUMENTS).getAbsolutePath();
    // 图片存储
    public static final String BasePicPath = App.getInstance().getExternalFilesDir(Environment
            .DIRECTORY_PICTURES).getAbsolutePath();

    // 数据库数据导出文件夹路径
    public static final String DB_ExportPath = Environment.getExternalStoragePublicDirectory
            (Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
    /**
     * 错误日志文件路径
     */
    public static final String LogPath = BaseFilePath + File.separator + "log";


    /**
     * 人脸比对模型
     */
    public static final String ModelPath = BaseFilePath + File.separator + "model";
    public static final String F_MODEL_SMALL = ModelPath + File.separator + "model_small.xml.gz";
    public static final String F_MODEL_SMALL_SUFFIX = "wis_alignment_0";

    public static final String F_DETECTOR_DAT_N = "fdetector_model.dat";
    public static final String F_DETECTOR_PROTO_N = "file1-proto";
    public static final String F_DETECTOR_MODEL_N = "file2-model";

//	public static final String M_BASE = RootFile +"base.dat";

//	public static final String M_LIC = RootFile+ ".license";

}
