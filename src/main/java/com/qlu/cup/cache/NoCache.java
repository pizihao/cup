package com.qlu.cup.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: cup
 * @description: 没有缓存的时候
 * @author: liuwenhao
 * @create: 2021-05-29 21:19
 **/
public class NoCache implements Cacheable {

    protected static final Map<String, Map<String, Object>> CACHE_POOL_POOL = new HashMap<>();

    public Object putCache(String key, Object obj) {
        return null;
    }

    @Override
    public Object putCache(String name, String key, Object obj) {
        return null;
    }

    @Override
    public void cleanCache() {

    }

    @Override
    public Object getCache(String nameId) {
        return null;
    }

    @Override
    public Object getCache(String name , String nameId) {
        return null;
    }
}