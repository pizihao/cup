package com.qlu.cup.makers.update;


import com.qlu.cup.makers.maker.SqlMaker;

/**
 * @program: cup
 * @description: 更新接口
 * @author: liuwenhao
 * @create: 2020-12-21 16:13
 **/
public interface Update extends SqlMaker {

    /**
     * update set 更新
     * @param entity
     * @param ignoreNull 是否忽略null值
     * @return
     */
    Update set(final Object entity, boolean ignoreNull);

}
