package com.qlu.cup.reflection.wrapper;


import com.qlu.cup.reflection.MetaObject;

/**
 * 对象包装器工厂
 */
public interface ObjectWrapperFactory {

    //有没有包装器
    boolean hasWrapperFor(Object object);

    //得到包装器
    ObjectWrapper getWrapperFor(MetaObject metaObject, Object object);

}
