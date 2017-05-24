package com.common.cache;

import android.graphics.Bitmap;

import java.lang.ref.Reference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by JamesP949 on 2017/5/16.
 * Function:
 *
 * Base memory cache. Implements common functionality for memory cache. Provides object references (
 * {@linkplain Reference not strong}) storing.
 */

public abstract class BaseMemoryCache implements MemoryCache {

    /** Stores not strong references to objects */
    private final Map<String, Reference<Bitmap>> softMap = Collections.synchronizedMap(new HashMap<String, Reference<Bitmap>>());

    @Override
    public Bitmap get(String key) {
        Bitmap result = null;
        Reference<Bitmap> reference = softMap.get(key);
        if (reference != null) {
            result = reference.get();
        }
        return result;
    }

    @Override
    public boolean put(String key, Bitmap value) {
        softMap.put(key, createReference(value));
        return true;
    }

    public Map.Entry<String, Reference<Bitmap>> getEntry(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        synchronized (this) {
            Set<Map.Entry<String, Reference<Bitmap>>> entrySet = softMap.entrySet();
            Iterator<Map.Entry<String, Reference<Bitmap>>> iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Reference<Bitmap>> next = iterator.next();
                if (next.getKey().endsWith(key)) {
                    return next;
                }
            }
        }
        return null;
    }

    @Override
    public Bitmap remove(String key) {
        Reference<Bitmap> bmpRef = softMap.remove(key);
        return bmpRef == null ? null : bmpRef.get();
    }

    @Override
    public Collection<String> keys() {
        synchronized (softMap) {
            return new HashSet<String>(softMap.keySet());
        }
    }

    @Override
    public void clear() {
        softMap.clear();
    }

    /** Creates {@linkplain Reference not strong} reference of value */
    protected abstract Reference<Bitmap> createReference(Bitmap value);
}
