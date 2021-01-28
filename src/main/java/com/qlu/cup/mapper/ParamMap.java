package com.qlu.cup.mapper;

import com.qlu.cup.bind.BindException;

import java.util.HashMap;

/**
 * @program: cup
 * @description: Map参数
 * @author: liuwenhao
 * @create: 2021-01-28 17:58
 **/
public class ParamMap<V> extends HashMap<String, V> {
    @Override
    public V get(Object key) {
        if (!super.containsKey(key)) {
            throw new BindException("Parameter '" + key + "' not found. Available parameters are " + keySet());
        }
        return super.get(key);
    }
}