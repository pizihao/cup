package com.qlu.cup.makers.query;

import com.qlu.cup.makers.maker.SqlMaker;
import java.util.List;

/**
 * @program: cup
 * @description: 查询接口
 * @author: liuwenhao
 * @create: 2020-12-21 16:13
 **/
public interface Query extends SqlMaker {

    /**
     * 排序条件
     */
    enum Order {
        DESC,
        ASC
    }

    void addQueryColumns(List<String> selectColumns);

    /**
     * 排序
     * @param orderBy
     * @param type:   ASC/DESC
     */
    Query orderBy(Order type, String... orderBy);

    /**
     * limit
     * @param line
     * @param num
     * @return
     */
    Query limit(int line, int num);

    /**
     * 分组
     * @param s
     * @return
     */
    Query groupBy(String s);

}
