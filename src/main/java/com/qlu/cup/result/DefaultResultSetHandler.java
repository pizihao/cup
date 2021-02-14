package com.qlu.cup.result;

import com.qlu.cup.bind.Configuration;
import com.qlu.cup.context.ErrorContext;
import com.qlu.cup.executor.Executor;
import com.qlu.cup.mapper.BoundSql;
import com.qlu.cup.mapper.ParameterHandler;
import com.qlu.cup.session.RowBounds;
import com.qlu.cup.statement.ParameterMapping;
import com.qlu.cup.type.TypeHandler;
import com.qlu.cup.type.TypeHandlerRegistry;

import java.lang.reflect.Constructor;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;


/**
 * 默认Map结果处理器
 */
public class DefaultResultSetHandler implements ResultSetHandler {
    private final Executor executor;
    private final Configuration configuration;
    private final RowBounds rowBounds;
    private final ParameterHandler parameterHandler;
    private final ResultProcessor resultHandler;
    private final BoundSql boundSql;
    private final TypeHandlerRegistry typeHandlerRegistry;

    private final Map<String, String> ancestorColumnPrefix = new HashMap<String, String>();

    // multiple resultsets
    private final Map<String, ResultMapping> nextResultMaps = new HashMap<String, ResultMapping>();

    public DefaultResultSetHandler(Executor executor, ParameterHandler parameterHandler, ResultProcessor resultHandler, BoundSql boundSql,
                                   RowBounds rowBounds) {
        this.executor = executor;
        this.configuration = Configuration.getConfiguration(boundSql.getEnvironment());
        this.rowBounds = rowBounds;
        this.parameterHandler = parameterHandler;
        this.boundSql = boundSql;
        this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
//        this.objectFactory = configuration.getObjectFactory();
        this.resultHandler = resultHandler;
    }

    @Override
    public <E> List<E> handleResultSets(Statement stmt) throws SQLException {
        return null;
    }

    @Override
    public void handleOutputParameters(CallableStatement cs) throws SQLException {

    }
}
