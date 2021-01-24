package com.qlu.cup.makers.update;

import com.qlu.cup.makers.maker.AbstractSqlMaker;
import com.qlu.cup.util.ClassUtils;
import com.qlu.cup.util.StringUtils;
import org.springframework.util.Assert;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @program: cup
 * @description: 默认的更新实现
 * @author: liuwenhao
 * @create: 2020-12-21 16:13
 **/
public class DefaultUpdate extends AbstractSqlMaker implements Update {

    private List<String> updataColumn;

    @Override
    public Update target(Class entity) {
        super.target(entity);
        return this;
    }

    /**
     * @param entity
     * @param ignoreNull 是否忽略null值
     * @return
     */
    @Override
    public Update set(final Object entity, final boolean ignoreNull) {
        Assert.notNull(entity);
        //重置被更新字段列表
        this.updataColumn = new ArrayList<>();
        List<String> columnNames = new ArrayList<>(entityTableRowMapper.getColumnNames());
        Map<String, Field> columnFieldMapper = entityTableRowMapper.getColumnFieldMapper();
        List<Object> values = new ArrayList<>();
        for (int i = 0; i < columnNames.size(); i++) {
            String columnName = columnNames.get(i);
            if (!sqlColumns.contains(columnName)) {
                continue;
            }
            Field field = columnFieldMapper.get(columnName);
            Object value = ClassUtils.getValue(entity, field);
            //如果class中的值是null，并且设置忽略null，跳过
            if (ignoreNull && value == null) {
                continue;
            }
            updataColumn.add(columnName);
            values.add(value);
        }
        sqlValues = values.toArray();
        return this;
    }

    @Override
    public String makeSql() {
        Assert.isTrue(updataColumn.size() != 0, "没有要更新的字段");
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(getTableName()).append(StringUtils.SPACE);
        sql.append("SET ");
        for (int i = 0; i < updataColumn.size(); i++) {
            String column = StringUtils.sqlColumn(updataColumn.get(i));
            if (i == 0) {
                sql.append(StringUtils.append(column, StringUtils.EQ));
            } else {
                sql.append(StringUtils.append(StringUtils.COMMA, column, StringUtils.EQ));
            }
        }
        sql.append(sqlWhere());
        return sql.toString();
    }

    @Override
    public List<Object> makeSqlValue() {
        return Arrays.asList(sqlValues);
    }

}
