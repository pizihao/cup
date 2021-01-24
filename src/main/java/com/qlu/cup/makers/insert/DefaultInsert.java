package com.qlu.cup.makers.insert;

import com.qlu.cup.makers.maker.AbstractSqlMaker;
import com.qlu.cup.makers.maker.SqlMaker;
import com.qlu.cup.makers.where.Where;
import  com.qlu.cup.util.ClassUtils;
import  com.qlu.cup.util.StringUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @program: cup
 * @description: 默认的插入实现
 * @author: liuwenhao
 * @create: 2020-12-21 16:13
 **/
public class DefaultInsert extends AbstractSqlMaker implements Insert {

    private List<String> insertColumns;

    private List<Object> insertColumnValues = new ArrayList();

    /**
     * 插入数据的数量
     */
    private int insertCount = 0;

    /**
     * 插入数据
     *
     * @param entity
     * @return
     */
    @Override
    public boolean insert(Object entity) {
        Assert.notNull(entity, "插入对象不能为 NULL");
        Map<String, Field> columnFieldMapper = entityTableRowMapper.getColumnFieldMapper();
        insertColumns = new ArrayList(columnFieldMapper.size());
        for (Map.Entry<String, Field> stringFieldEntry : columnFieldMapper.entrySet()) {
            Field field = stringFieldEntry.getValue();
            Object value = ClassUtils.getValue(entity, field);
            insertColumns.add(StringUtils.sqlColumn(stringFieldEntry.getKey()));
            insertColumnValues.add(value);
        }
        //添加插入数量
        insertCount++;
        return true;
    }

    @Override
    protected String makeSql() {
        StringBuilder builder = new StringBuilder();
        int size = insertColumns.size();
        builder.append("INSERT INTO ").append(getTableName()).append(StringUtils.SPACE);
        builder.append(StringUtils.append("( ", StringUtils.join(insertColumns, ", "), " ) "));
        builder.append("VALUES ");
        for (int i = 0; i < insertCount; i++) {
            builder.append("( ");
            String[] repeat = StringUtils.repeat("?", size);
            builder.append(StringUtils.join(Arrays.asList(repeat), ", "));
            builder.append(" )");
            if (i != insertCount - 1) {
                builder.append(StringUtils.COMMA);
            }
        }
        builder.append(";");
        return builder.toString();
    }

    @Override
    protected List<Object> makeSqlValue() {
        if (insertColumnValues == null) {
            return new ArrayList<>();
        }
        return insertColumnValues;
    }

    @Override
    public SqlMaker where(Where... wheres) {
        throw new UnsupportedOperationException("不支持的操作");
    }

}
