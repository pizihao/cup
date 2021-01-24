package com.qlu.cup.makers.query;

import com.qlu.cup.makers.maker.AbstractSqlMaker;
import com.qlu.cup.builder.annotations.EntityTableRowMapper;
import com.qlu.cup.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @program: cup
 * @description: 默认的查询实现
 * @author: liuwenhao
 * @create: 2020-12-21 16:13
 **/
public class DefaultQuery extends AbstractSqlMaker implements Query {

    private String sqlLimit = StringUtils.BLANK;
    private String sqlOrderBy = StringUtils.BLANK;
    private String sqlGroupBy = StringUtils.BLANK;

    /**
     * 要查询作为结果的字段
     */
    private List<String> selectColumns = null;

    /**
     * sql
     */
    private StringBuilder sql = new StringBuilder();

    /**
     * 手动设定要查询的字段
     * @param selectColumns
     */
    @Override
    public void addQueryColumns(List<String> selectColumns) {
        //不为空，并且list中有值的时候
        if (selectColumns != null && selectColumns.size() != 0) {
            this.selectColumns = selectColumns;
        }
    }

    @Override
    public Query orderBy(Order type, String... orderBy) {
        String[] columns = new String[orderBy.length];
        for (int i = 0; i < orderBy.length; i++) {
            columns[i] = getColumnName(orderBy[i]);
        }
        sqlOrderBy = " ORDER BY " +
                StringUtils.join(Arrays.asList(columns), StringUtils.COMMA) +
                StringUtils.SPACE + type.name();
        return this;
    }

    @Override
    public Query limit(int line, int num) {
        this.sqlLimit = " LIMIT " + line + StringUtils.COMMA + num + StringUtils.SPACE;
        return this;
    }

    @Override
    public Query groupBy(String column) {
        this.sqlGroupBy = " GROUP BY " + column;
        return this;
    }

    /**
     * @return
     */
    @Override
    protected String makeSql() {
        EntityTableRowMapper entityTableRowMapper = getEntityTableRowMapper();
        Set<String> columnNames = entityTableRowMapper.getColumnNames();
        if (selectColumns == null) {
            selectColumns = new ArrayList<>(columnNames.size());
            for (String columnName : columnNames) {
                selectColumns.add(StringUtils.sqlColumn(columnName));
            }
        }
        sql.append("SELECT ")
                .append(StringUtils.join(selectColumns, StringUtils.COMMA))
                .append(" FROM ").append(StringUtils.SPACE)
                .append(getTableName())
                .append(sqlWhere())
                .append(sqlGroupBy)
                .append(sqlOrderBy)
                .append(sqlLimit);
        return sql.toString();
    }

    @Override
    protected List<Object> makeSqlValue() {
        return Arrays.asList(sqlValues);
    }

}
