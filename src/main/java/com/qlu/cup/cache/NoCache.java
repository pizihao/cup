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

    public static Map<String, Object> cachePool;

    @Override
    public Object putCache(String key, Object obj) {
        return null;
    }

    @Override
    public void cleanCache() {

    }

    @Override
    public Object getCache(String nameId) {
        return null;
    }
}