package com.qlu.cup.builder;

import com.qlu.cup.bind.Configuration;
import com.qlu.cup.mapper.BoundSql;
import com.qlu.cup.mapper.SqlSource;
import com.qlu.cup.statement.ParameterMapping;

import java.util.List;

/**
 * 静态SQL源码
 */
public class StaticSqlSource implements SqlSource {

    private String sql;
    private List<ParameterMapping> parameterMappings;
    private Configuration configuration;

    public StaticSqlSource(Configuration configuration, String sql) {
        this(configuration, sql, null);
    }

    public StaticSqlSource(Configuration configuration, String sql, List<ParameterMapping> parameterMappings) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
        this.configuration = configuration;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return BoundSql.getBoundSql(configuration, sql, parameterMappings, parameterObject);
    }

}