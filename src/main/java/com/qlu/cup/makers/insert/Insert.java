package com.qlu.cup.makers.insert;

import com.qlu.cup.makers.maker.SqlMaker;

/**
 * @program: cup
 * @description: 插入数据接口
 * @author: liuwenhao
 * @create: 2020-12-21 16:13
 **/
public interface Insert extends SqlMaker {

    /**
     * @description: 插入
     * @param value 插入的信息
     * @author liuwenaho
     * @date 2021/1/23 16:12
     */
    boolean insert(final Object value);

}
