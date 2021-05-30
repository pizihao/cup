package com.qlu.cup.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: cup
 * @description: cup默认缓存类
 * @author: liuwenhao
 * @create: 2021-05-29 20:24
 **/
public class CupCache implements Cacheable {

    protected static final Map<String, Map<String, Object>> CACHE_POOL_POOL = new HashMap<>();


    private Map<String, Object> getCachePool(String namespace) {
        Map<String, Object> cachePool = CACHE_POOL_POOL.get(namespace);
        if (cachePool == null){
            putCache(namespace, new HashMap<>());
        }
        return CACHE_POOL_POOL.get(namespace);
    }

    /**
     * 生成缓存key
     *
     * @param name 命名空间或方法名
     * @return 缓存key
     * @author liuwenaho
     * @date 2021/5/29 21:02
     */
    protected String generateKey(String name) {
        return name;
    }

    @Override
    public void cleanCache() {
        CACHE_POOL_POOL.clear();
    }

    @Override
    public Object putCache(String name, String key, Object obj) {
        return getCachePool(name).put(key, obj);
    }

    @Override
    public Object removeCache(String name, String key) {
        return CACHE_POOL_POOL.put(name, null);
    }

    public Object putCache(String key, Map<String, Object> obj) {
        return CACHE_POOL_POOL.put(key, obj);
    }

    /**
     * 如果缓存中存在意相应key，则不添加
     *
     * @param key  缓存key
     * @param obj  数据
     * @param name 命名空间
     * @return 如果指定的键尚未与值关联（或映射为null），则将其与给定值关联并返回null，否则返回当前值
     * @author liuwenaho
     * @date 2021/5/29 20:55
     */
    public Object putIfAbsentCache(String name, String key, Object obj) {
        return getCachePool(name).putIfAbsent(key, obj);
    }

    /**
     * 获取所有的缓存信息
     *
     * @return 缓存Map
     * @author liuwenaho
     * @date 2021/5/29 21:00
     */
    public Map<String, Map<String, Object>> getCachePoolPool() {
        return CACHE_POOL_POOL;
    }

    @Override
    public Object getCache(String key) {
        return CACHE_POOL_POOL.get(key);
    }

    @Override
    public Object getCache(String name, String key) {
        return getCachePool(name).get(key);
    }
}