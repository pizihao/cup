package com.qlu.cup.makers.delete;

import com.qlu.cup.makers.maker.AbstractSqlMaker;
import com.qlu.cup.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @program: cup
 * @description: 默认的删除记录实现
 * @author: liuwenhao
 * @create: 2020-12-21 16:13
 **/
public class DefaultDelete extends AbstractSqlMaker implements Delete {

    @Override
    protected String makeSql() {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ")
                .append(getTableName())
                .append(StringUtils.SPACE)
                .append(sqlWhere());
        return sql.toString();
    }

    @Override
    protected List<Object> makeSqlValue() {
        return Arrays.asList(sqlValues);
    }

}
