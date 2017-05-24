package com.wis.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.socks.library.KLog;
import com.wis.application.App;
import com.wis.config.AppConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by JamesP949 on 2017/3/14.
 * Function:拷贝读卡器相关信息及人脸比对模型
 */
public class CopyFileToSD {
    private static final int ASSETS_SUFFIX_BEGIN = 0;
    private static final int ASSETS_SUFFIX_END = 9;
    private static boolean flag;  // 拷贝模型成功标志

    /**
     * 初始化设备数据库
     *
     * @param context
     * @return
     */
    public static boolean initDB(Context context) {
        File file = new File(AppConfig.ModelPath);//临时文件夹

        if (!file.exists()) {
            file.mkdir();
        }

//        initCopyReaderFiles(context);
        initCopyModelFiles(context.getApplicationContext());
        KLog.e("loadModule---S" + System.currentTimeMillis());
        App.getInstance().loadWisMobile();
        KLog.e("loadModule---E" + System.currentTimeMillis());
        return flag;
    }

   /* *//**
     * 读卡器相关
     *
     * @param context
     *//*
    private static void initCopyReaderFiles(Context context) {
        File file = new File(AppConfig.BASE);
        File file1 = new File(AppConfig.LIC);
       
        //检测文件是否存在，否则拷贝
        if (!file.exists()) {
            Init(context, R.raw.base, AppConfig.BASE);
        } else flag = true;
        if (!file1.exists()) {
            Init(context, R.raw.license, AppConfig.LIC);
        } else flag = true;
    }
*/

    /**
     * 人脸比对模型相关
     *
     * @param context
     */
    private static void initCopyModelFiles(Context context) {
        KLog.i("start copy files----------> ");
        copyBigDataBase(context);
        copyBigDataToSD(context, AppConfig.F_DETECTOR_DAT_N);
        copyBigDataToSD(context, AppConfig.F_DETECTOR_PROTO_N);
        copyBigDataToSD(context, AppConfig.F_DETECTOR_MODEL_N);//file2-model改为不下载，直接copy
//        initModelFiles(context);
        KLog.i("end copy files----------> ");

    }


  /*  private static void initModelFiles(Context context) {
        File file1 = new File(AppConfig.M_LIC);
        //检测文件是否存在，否则拷贝
        if (!file1.exists()) {
            Init(context, R.raw.ef9552, AppConfig.M_LIC);
        } else flag = true;
    }*/

    /**
     * 拷贝数据库
     *
     * @param context
     * @param rawId
     * @param path
     * @return
     */
    private static void Init(Context context, @NonNull int rawId, String path) {
        InputStream input = null;
        OutputStream output = null;
        // 输出路径

        // 从资源中读取数据库流
        input = context.getResources().openRawResource(rawId);

        try {
            output = new FileOutputStream(path);

            // 拷贝到输出流
            byte[] buffer = new byte[2048];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            flag = true;
        } catch (FileNotFoundException e) {
            KLog.e(e.getMessage());
            flag = false;
        } catch (IOException e) {
            KLog.e(e.getMessage());
            flag = false;
        } finally {
            // 关闭输出流
            try {
                output.flush();
                output.close();
                input.close();
            } catch (IOException e) {
            }

        }
    }


    private static void copyBigDataBase(Context context) {
        InputStream myInput = null;
        OutputStream myOutput = null;
        KLog.i("start copy file " + AppConfig.F_MODEL_SMALL);
        File f = new File(AppConfig.F_MODEL_SMALL);
        if (f.exists()) {
            KLog.i("file exists " + AppConfig.F_MODEL_SMALL);
            flag = true;
            return;
        }

        try {
            myOutput = new FileOutputStream(AppConfig.F_MODEL_SMALL);
            for (int i = ASSETS_SUFFIX_BEGIN; i < ASSETS_SUFFIX_END + 1; i++) {
                String filename = AppConfig.F_MODEL_SMALL_SUFFIX + i;
                KLog.i("start copy files--- " + filename);
                myInput = context.getAssets().open(filename);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }
                myOutput.flush();
                myInput.close();
            }
            KLog.i("end copy file " + AppConfig.F_MODEL_SMALL);
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
            flag = false;
        } finally {
            try {
                myOutput.close();
            } catch (IOException e) {
            }
        }
    }

    private static void copyBigDataToSD(Context context, String strOutFileName) {
        KLog.i("start copy file " + strOutFileName);
        String tmpFile = AppConfig.ModelPath + File.separator + strOutFileName;
        File f = new File(tmpFile);
        if (f.exists()) {
            KLog.i("file exists " + strOutFileName);
            flag = true;
            return;
        }

        InputStream myInput = null;
        OutputStream myOutput = null;
        try {
            myOutput = new FileOutputStream(tmpFile);
            myInput = context.getAssets().open(strOutFileName);
            byte[] buffer = new byte[1024];
            int length = myInput.read(buffer);
            while (length > 0) {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
            flag = false;
        } finally {
            try {
                myOutput.flush();
                myInput.close();
                myOutput.close();
                KLog.i("end copy file " + strOutFileName);
            } catch (IOException e) {

            }
        }
    }
}
