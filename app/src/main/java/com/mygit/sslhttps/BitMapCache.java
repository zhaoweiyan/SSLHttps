package com.mygit.sslhttps;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by admin on 2015/11/16.
 */
public class BitMapCache implements ImageLoader.ImageCache {
    private LruCache<String, Bitmap> cache;

    private static BitMapCache bitMapCache;

    private BitMapCache() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        cache = new LruCache<String, Bitmap>(8 * 1024 * 1024) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };
    }

    public static BitMapCache getInstance() {
        if (bitMapCache == null) {
            bitMapCache = new BitMapCache();
        }
        return bitMapCache;
    }

    @Override
    public Bitmap getBitmap(String url) {
        return cache.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        cache.put(url, bitmap);
    }

    public int getBitmapCacheSize() {
        return cache.size();
    }

    public void removeBitmapSize() {
        cache.evictAll();
    }

    public void clearBitmap(String url, int maxWidth, int maxHeight) {
        cache.remove(getCacheKey(url, maxWidth, maxHeight));
    }

    //同ImageLoader里私有方法
    private static String getCacheKey(String url, int maxWidth, int maxHeight) {
        return (new StringBuilder(url.length() + 12)).append("#W").append(maxWidth).append("#H").append(maxHeight).append(url).toString();
    }
}
