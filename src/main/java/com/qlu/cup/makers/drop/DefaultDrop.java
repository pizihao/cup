package com.qlu.cup.makers.drop;

import com.qlu.cup.makers.maker.AbstractSqlMaker;
import com.qlu.cup.makers.maker.SqlMaker;
import com.qlu.cup.makers.where.Where;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: cup
 * @description: 默认的删除表实现
 * @author: liuwenhao
 * @create: 2020-12-21 16:13
 **/
public class DefaultDrop extends AbstractSqlMaker implements Drop {

    public static final String DROP_TABLE = "DROP TABLE %s;";

    @Override
    public String makeSql() {
        return String.format(DROP_TABLE, entityTableRowMapper.getTableName());
    }

    @Override
    public List<Object> makeSqlValue() {
        return new ArrayList<>();
    }

    @Override
    public SqlMaker where(Where... wheres) {
        throw new UnsupportedOperationException("不支持的操作");
    }
}
