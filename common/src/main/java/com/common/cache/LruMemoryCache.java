package com.common.cache;

import android.graphics.Bitmap;
import android.os.Build;

import com.socks.library.KLog;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by JamesP949 on 2017/5/15.
 * Function:
 * A cache that holds strong references to a limited number of Bitmaps. Each time a Bitmap is
 * accessed, it is moved to
 * the head of a queue. When a Bitmap is added to a full cache, the Bitmap at the end of that
 * queue is evicted and may
 * become eligible for garbage collection.<br />
 * <br />
 * <b>NOTE:</b> This cache uses only strong references for stored Bitmaps.
 */

public class LruMemoryCache implements MemoryCache {
    private final LinkedHashMap<String, Bitmap> map;
    private final int maxSize;
    /**
     * Size of this cache in bytes
     */
    private int size;

    /**
     * @param maxSize Maximum sum of the sizes of the Bitmaps in this cache
     */
    public LruMemoryCache(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        }
        this.maxSize = maxSize;
        this.map = new LinkedHashMap<>(0, 0.75f, true);
    }

    /**
     * Caches {@code Bitmap} for {@code key}. The Bitmap is moved to the head of the queue.
     */
    @Override
    public boolean put(String key, Bitmap value) {
        if (key == null || value == null) {
            throw new NullPointerException("key == null || value == null");
        }
        synchronized (this) {
            size += sizeOf(key, value);
            Bitmap previous = map.put(key, value);
            if (previous != null) {
                size -= sizeOf(key, previous);
            }
        }
        trimToSize(maxSize);
        return true;
    }

    public Map.Entry<String, Bitmap> getEntry(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        synchronized (this) {
            Set<Map.Entry<String, Bitmap>> entrySet = map.entrySet();
            Iterator<Map.Entry<String, Bitmap>> iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Bitmap> next = iterator.next();
                if (next.getKey().endsWith(key)) {
                    return next;
                }
            }
        }
        return null;
    }

    /**
     * Remove the eldest entries until the total of remaining entries is at or below the
     * requested size.
     *
     * @param maxSize the maximum size of the cache before returning. May be -1 to evict even
     *                0-sized elements.
     */
    private void trimToSize(int maxSize) {
        while (true) {
            String key;
            Bitmap value;
            synchronized (this) {
                if (size < 0 || (map.isEmpty() && size != 0)) {
                    throw new IllegalStateException(getClass().getName() + ".sizeOf() is " +
                            "reporting inconsistent results!");
                }

                if (size <= maxSize || map.isEmpty()) {
                    break;
                }

                Map.Entry<String, Bitmap> toEvict = map.entrySet().iterator().next();
                if (toEvict == null) {
                    break;
                }
                key = toEvict.getKey();
                value = toEvict.getValue();

                map.remove(key);
//                KLog.e("Lru---key:" + key);
                size -= sizeOf(key, value);
//                KLog.e("Lru---size:" + size);
            }
        }
    }

    /**
     * Returns the Bitmap for {@code key} if it exists in the cache. If a Bitmap was returned, it
     * is moved to the head
     * of the queue. This returns null if a Bitmap is not cached.
     */
    @Override
    public final Bitmap get(String key) {
        if (key == null)
            throw new NullPointerException("key == null");
        synchronized (this) {
            return map.get(key);
        }
    }

    /**
     * Removes the entry for {@code key} if it exists.
     */
    @Override
    public final Bitmap remove(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        synchronized (this) {
            Bitmap previous = map.remove(key);
            if (previous != null) {
                size -= sizeOf(key, previous);
            }
            KLog.e("remove---key:" + key );
            return previous;
        }
    }

    @Override
    public Collection<String> keys() {
        synchronized (this) {
            return new HashSet<String>(map.keySet());
        }
    }

    @Override
    public void clear() {
        trimToSize(-1); // -1 will evict 0-sized elements
    }

    /**
     * Returns the size {@code Bitmap} in bytes.
     * <p/>
     * An entry's size must not change while it is in the cache.
     */
    private int sizeOf(String key, Bitmap value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return value.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return value.getByteCount();
        }
        return value.getRowBytes() * value.getHeight();
    }

    @Override
    public synchronized final String toString() {
        return String.format("LruCache[maxSize=%d]", maxSize);
    }
}
