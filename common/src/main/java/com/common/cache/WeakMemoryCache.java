package com.common.cache;

import android.graphics.Bitmap;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Created by JamesP949 on 2017/5/16.
 * Function:
 * Memory cache with {@linkplain WeakReference weak references} to
 * {@linkplain android.graphics.Bitmap bitmaps}<br />
 * <br />
 * <b>NOTE:</b> This cache uses only weak references for stored Bitmaps.
 */

public class WeakMemoryCache extends BaseMemoryCache {
    @Override
    protected Reference<Bitmap> createReference(Bitmap value) {
        return new WeakReference<Bitmap>(value);
    }
}
