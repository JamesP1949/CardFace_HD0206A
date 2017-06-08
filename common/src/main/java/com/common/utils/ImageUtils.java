package com.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by ybbz on 16/8/28.
 */
public class ImageUtils {
    private static final int REQUEST_PHOTO_CUT = 0x003;

    public static byte[] DrawableToBytes(Context context, int id) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = ((BitmapDrawable) context.getResources().getDrawable(id)).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    public static byte[] BitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap BytesToBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    public static void intent2Cut(Activity activity, Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "false");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);
        ActivityCompat.startActivityForResult(activity, intent, REQUEST_PHOTO_CUT,
                null);
    }

    public static void intent2Pick(Activity activity, int requestCode) {
        // 本地图片
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        ActivityCompat.startActivityForResult(activity, intent, requestCode, null);
    }

    public static void intent2Take(Activity activity, int requestCode, File tempFile) {
        // 调用系统的拍照功能
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 指定调用相机拍照后照片的储存路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(tempFile));
        ActivityCompat.startActivityForResult(activity, intent, requestCode, null);
    }

    /**
     * 根据要求的宽高 压缩图片
     *
     * @param filePath       原图的文件路径
     * @param requiredWidth
     * @param requiredHeight
     * @return
     */
    public static Bitmap readBitmapFromFile(String filePath, int requiredWidth, int
            requiredHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        float outW = options.outWidth;
        float outH = options.outHeight;

        int scaledSize = (int) Math.min(outW / requiredWidth, outH / requiredHeight);
        if (scaledSize < 1)
            scaledSize = 1;
        options.inSampleSize = scaledSize;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        if (bitmap == null) return null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
       /* while (getBitmapSize(bitmap) > 30 * 1024) { // 30Kb界限
            bos.reset();
            quality -= 10;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
        }*/
        return bitmap;
    }

    /**
     * 获取图片在内存中的大小
     *
     * @param bitmap
     * @return
     */
    public static long getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
        }
    }
}
