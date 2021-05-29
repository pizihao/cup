package com.qlu.cup.cache;


/**
 * 缓存接口
 *
 * @program: cup
 * @description:
 * @author: liuwenhao
 * @create: 2021-05-29 20:07
 **/
public interface Cacheable {

    /**
     * 修改或添加缓存中的数据
     *
     * @param key 缓存key
     * @param obj 数据
     * @return 返回修改前的数据
     * @author liuwenaho
     * @date 2021/5/29 20:16
     */
    Object putCache(String key, Object obj);

    /**
     * 删除key对应的数据
     *
     * @param key 缓存key
     * @return 返回删除前的数据
     * @author liuwenaho
     * @date 2021/5/29 20:17
     */
    default Object removeCache(String key) {
        return putCache(key, null);
    }

    /**
     * 清空缓存
     *
     * @author liuwenaho
     * @date 2021/5/29 20:20
     */
    void cleanCache();

    /**
     * 获取key对应的数据
     *
     * @param key 缓存key
     * @return Object 数据
     * @author liuwenaho
     * @date 2021/5/29 21:01
     */
    Object getCache(String key);
}
