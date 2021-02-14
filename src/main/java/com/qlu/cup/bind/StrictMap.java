package com.qlu.cup.bind;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: cup
 * @description: 自定义map
 * @author: liuwenhao
 * @create: 2021-02-14 14:29
 **/
public class StrictMap<V> extends HashMap<String, V> {
    private static final long serialVersionUID = -4950446264854982944L;
    private String name;

    public StrictMap(String name, int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        this.name = name;
    }

    public StrictMap(String name, int initialCapacity) {
        super(initialCapacity);
        this.name = name;
    }

    public StrictMap(String name) {
        super();
        this.name = name;
    }

    public StrictMap(String name, Map<String, ? extends V> m) {
        super(m);
        this.name = name;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V put(String key, V value) {
        if (containsKey(key)) {
            //如果已经存在此key了，直接报错
            throw new IllegalArgumentException(name + " already contains value for " + key);
        }
        if (key.contains(".")) {
            //如果有.符号，取得短名称，大致用意就是包名不同，类名相同，提供模糊查询的功能
            final String shortKey = getShortName(key);
            if (super.get(shortKey) == null) {
                //如果没有这个缩略，则放一个缩略
                super.put(shortKey, value);
            } else {
                //如果已经有此缩略，表示模糊，放一个Ambiguity型的
                super.put(shortKey, (V) new Ambiguity(shortKey));
            }
        }
        //再放一个全名
        return super.put(key, value);
    }

    @Override
    public V get(Object key) {
        V value = super.get(key);
        //如果找不到相应的key，直接报错
        if (value == null) {
            throw new IllegalArgumentException(name + " does not contain value for " + key);
        }
        //如果是模糊型的，也报错，提示用户
        if (value instanceof Ambiguity) {
            throw new IllegalArgumentException(((Ambiguity) value).getSubject() + " is ambiguous in " + name
                    + " (try using the full name including the namespace, or rename one of the entries)");
        }
        return value;
    }

    private String getShortName(String key) {
        final String[] keyparts = key.split("\\.");
        return keyparts[keyparts.length - 1];
    }

    protected static class Ambiguity {
        //提供一个主题
        private String subject;

        public Ambiguity(String subject) {
            this.subject = subject;
        }

        public String getSubject() {
            return subject;
        }
    }
}