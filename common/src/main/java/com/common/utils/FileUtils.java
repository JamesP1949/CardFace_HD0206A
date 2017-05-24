package com.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.socks.library.KLog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by JamesP949 on 2017/4/27.
 * Function:
 */

public class FileUtils {
    /**
     * @param context
     * @param filename
     * @param bitmap
     * @return
     */
    public static String saveBitmap2File(Context context, String filename, Bitmap bitmap) {
        if (bitmap == null) return "";
        String dirPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                .getAbsolutePath();
        File dir = new File(dirPath);
        if (dir.exists())
            dir.mkdirs();

        String targetFileName = filename + ".jpg";
        File bpFile = new File(dir, targetFileName);
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
            while (bos.size() > 60 * 1024) {
                bos.reset();
                quality -= 5;
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
            }
            byte[] data = bos.toByteArray();
            FileOutputStream fos = new FileOutputStream(bpFile);
            fos.write(data, 0, data.length);
            fos.flush();
            fos.close();
            KLog.e("path---:" + dirPath + "/" + targetFileName);
            return dirPath + "/" + targetFileName;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
