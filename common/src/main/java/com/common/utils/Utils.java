package com.common.utils;

import android.content.Context;
import android.os.Environment;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.socks.library.KLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by JamesP949 on 2017/3/9.
 * Function:
 */

public class Utils {
    private static SimpleDateFormat format = new SimpleDateFormat(
            "yyyy-MM-dd HH-mm-ss");// 用于格式化日期,作为日志文件名的一部分


    private static SimpleDateFormat format_ = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    private static SimpleDateFormat Format = new SimpleDateFormat(
            "yyyyMMddHHmmss");

    public static String dateformat2String(long time) {
        return format.format(new Date(time));
    }

    public static String dateformat_2String(long time) {
        return format_.format(new Date(time));
    }

    public static String dateFormat2String(long time) {
        return Format.format(new Date(time));
    }

    /**
     * 关闭键盘
     *
     * @param context
     * @param edit
     */
    public static void hideSoftInput(Context context, EditText edit) {
        KLog.e("关闭软键盘-----S");
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context
                .INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
        KLog.e("关闭软键盘-----E");
    }

    public static String saveErrInfo2File(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        ex.printStackTrace(pw);
        Throwable cause = ex.getCause();
        // 循环着把所有的异常信息写入writer中
        while (cause != null) {
            cause.printStackTrace(pw);
            cause = cause.getCause();
        }
        pw.close();// 记得关闭
        String result = writer.toString();
        sb.append(result);
        // 保存文件
        String time = format.format(new Date());
        String fileName = time + "_YY.log";
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        KLog.e("进入Error方法：" + Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED));
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            try {
                File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/wis/crash_log");
                if (!dir.exists())
                    dir.mkdir();

                FileOutputStream fos = new FileOutputStream(dir + "/"
                        + fileName);
                fos.write(sb.toString().getBytes());
                //发送给开发人员
//				sendCrashLog2PM(dir + fileName);
                fos.close();
                return fileName;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
