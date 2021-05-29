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

    public static Map<String, Object> cachePool = new HashMap<>();

    /**
     * 生成缓存key
     *
     * @param namespace  命名空间
     * @param methodName 方法名
     * @return 缓存key
     * @author liuwenaho
     * @date 2021/5/29 21:02
     */
    protected String generateKey(String namespace, String methodName) {
        return namespace + "." + methodName;
    }

    @Override
    public void cleanCache() {
        cachePool.clear();
    }

    @Override
    public Object putCache(String key, Object obj) {
        return cachePool.put(key, obj);
    }

    /**
     * 如果缓存中存在意相应key，则不添加
     *
     * @param key 缓存key
     * @param obj 数据
     * @return 如果指定的键尚未与值关联（或映射为null），则将其与给定值关联并返回null，否则返回当前值
     * @author liuwenaho
     * @date 2021/5/29 20:55
     */
    public Object putIfAbsentCache(String key, Object obj) {
        return cachePool.putIfAbsent(key, obj);
    }

    /**
     * 获取所有的缓存信息
     *
     * @return 缓存Map
     * @author liuwenaho
     * @date 2021/5/29 21:00
     */
    public Map<String, Object> getCachePool() {
        return cachePool;
    }

    @Override
    public Object getCache(String key) {
        return cachePool.get(key);
    }
}