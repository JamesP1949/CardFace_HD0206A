package com.common.cache;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.FragmentManager;

import com.common.utils.VersionUtils;

import static com.common.utils.ImageUtils.calculateInSampleSize;

/**
 * Created by JamesP949 on 2017/6/27.
 * Function:
 */

public class ImageReSize {
    private ImageCache mImageCache;
    private ImageCache.ImageCacheParams mImageCacheParams;
    private Bitmap mBitmap;


    public ImageReSize(Context context) {
    }

    public ImageCache getImageCache() {
        return mImageCache;
    }

    public void addImageCache(FragmentManager fragmentManager,
                              ImageCache.ImageCacheParams cacheParams) {
        mImageCacheParams = cacheParams;
        mImageCache = ImageCache.getInstance(fragmentManager, mImageCacheParams);
    }

    public Bitmap processBitmap(byte[] data, int reqWidth, int reqHeight) {
        return decodeBitmapFromBytes(data, reqWidth, reqHeight, mImageCache);
    }
    public static Bitmap decodeBitmapFromBytes(byte[] data, int reqWidth, int reqHeight,
                                               ImageCache cache) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        // If we're running on Honeycomb or newer, try to use inBitmap
        if (VersionUtils.hasHoneycomb()) {
            addInBitmapOptions(options, cache);
        }

        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static void addInBitmapOptions(BitmapFactory.Options options, ImageCache cache) {
        //BEGIN_INCLUDE(add_bitmap_options)
        // inBitmap only works with mutable bitmaps so force the decoder to
        // return mutable bitmaps.
        options.inMutable = true;

        if (cache != null) {
            // Try and find a bitmap to use for inBitmap
            Bitmap inBitmap = cache.getBitmapFromReusableSet(options);

            if (inBitmap != null) {
                options.inBitmap = inBitmap;
            }
        }
        //END_INCLUDE(add_bitmap_options)
    }


}
