package com.qlu.cup.result;

import com.qlu.cup.bind.Configuration;
import com.qlu.cup.context.ErrorContext;
import com.qlu.cup.executor.Executor;
import com.qlu.cup.executor.ExecutorException;
import com.qlu.cup.mapper.BoundSql;
import com.qlu.cup.mapper.ParameterHandler;
import com.qlu.cup.reflection.MetaObject;
import com.qlu.cup.reflection.factory.ObjectFactory;
import com.qlu.cup.session.RowBounds;
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
    private final ObjectFactory objectFactory;

    private final Map<String, String> ancestorColumnPrefix = new HashMap<String, String>();

    private final Map<String, ResultMapping> nextResultMaps = new HashMap<String, ResultMapping>();

    public DefaultResultSetHandler(Executor executor, ParameterHandler parameterHandler, ResultProcessor resultHandler, BoundSql boundSql,
                                   RowBounds rowBounds) {
        this.executor = executor;
        this.configuration = Configuration.getConfiguration(boundSql.getEnvironment());
        this.rowBounds = rowBounds;
        this.parameterHandler = parameterHandler;
        this.boundSql = boundSql;
        this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        this.resultHandler = resultHandler;
        this.objectFactory = configuration.getObjectFactory();
    }

    @Override
    public <E> List<E> handleResultSets(Statement stmt) throws SQLException {
        ErrorContext.instance().activity("handling results").object(boundSql.getID());

        final List<Object> multipleResults = new ArrayList<Object>();

        int resultSetCount = 0;
        ResultSetWrapper rsw = getFirstResultSet(stmt);

        List<ResultMap> resultMaps = boundSql.getResultMaps();
        int resultMapCount = resultMaps.size();
        validateResultMapsCount(rsw, resultMapCount);
        while (rsw != null && resultMapCount > resultSetCount) {
            ResultMap resultMap = resultMaps.get(resultSetCount);
            handleResultSet(rsw, resultMap, multipleResults, null);
            rsw = getNextResultSet(stmt);
            cleanUpAfterHandlingResultSet();
            resultSetCount++;
        }

        return (List<E>) collapseSingleResultList(multipleResults);
    }

    @Override
    public void handleOutputParameters(CallableStatement cs) throws SQLException {

    }

    private ResultSetWrapper getFirstResultSet(Statement stmt) throws SQLException {
        ResultSet rs = stmt.getResultSet();
        while (rs == null) {
            if (stmt.getMoreResults()) {
                rs = stmt.getResultSet();
            } else {
                if (stmt.getUpdateCount() == -1) {
                    break;
                }
            }
        }
        return rs != null ? new ResultSetWrapper(rs, configuration) : null;
    }

    private void validateResultMapsCount(ResultSetWrapper rsw, int resultMapCount) {
        if (rsw != null && resultMapCount < 1) {
            throw new ExecutorException("返回信息异常" + boundSql.getID());
        }
    }

    private void handleResultSet(ResultSetWrapper rsw, ResultMap resultMap, List<Object> multipleResults, ResultMapping parentMapping) throws SQLException {
        try {
            if (parentMapping != null) {
                handleRowValues(rsw, resultMap, null, RowBounds.DEFAULT, parentMapping);
            } else {
                if (resultHandler == null) {
                    //如果没有resultHandler
                    //新建DefaultResultHandler
                    DefaultResultHandler defaultResultHandler = new DefaultResultHandler(objectFactory);
                    //调用自己的handleRowValues
                    handleRowValues(rsw, resultMap, defaultResultHandler, rowBounds, null);
                    //得到记录的list
                    multipleResults.add(defaultResultHandler.getResultList());
                } else {
                    //如果有resultHandler
                    handleRowValues(rsw, resultMap, resultHandler, rowBounds, null);
                }
            }
        } finally {
            //最后别忘了关闭结果集，这个居然出bug了
            closeResultSet(rsw.getResultSet());
        }
    }

    private void handleRowValues(ResultSetWrapper rsw, ResultMap resultMap, ResultProcessor resultHandler, RowBounds rowBounds, ResultMapping parentMapping) throws SQLException {
        if (resultMap.hasNestedResultMaps()) {
            ensureNoRowBounds();
            checkResultHandler();
            handleRowValuesForNestedResultMap(rsw, resultMap, resultHandler, rowBounds, parentMapping);
        } else {
            handleRowValuesForSimpleResultMap(rsw, resultMap, resultHandler, rowBounds, parentMapping);
        }
    }

    private void closeResultSet(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException ignored) {
        }
    }

    private void ensureNoRowBounds() {
        if (configuration.isSafeRowBoundsEnabled() && rowBounds != null && (rowBounds.getLimit() < RowBounds.NO_ROW_LIMIT || rowBounds.getOffset() > RowBounds.NO_ROW_OFFSET)) {
            throw new ExecutorException("返回值分页异常");
        }
    }

    protected void checkResultHandler() {
        if (resultHandler != null && configuration.isSafeResultHandlerEnabled()) {
            throw new ExecutorException("返回值处理集异常");
        }
    }

    private void handleRowValuesForNestedResultMap(ResultSetWrapper rsw, ResultMap resultMap, ResultProcessor resultHandler, RowBounds rowBounds, ResultMapping parentMapping) throws SQLException {
        skipRows(rsw.getResultSet(), rowBounds);
    }

    private void skipRows(ResultSet rs, RowBounds rowBounds) throws SQLException {
        if (rs.getType() != ResultSet.TYPE_FORWARD_ONLY) {
            if (rowBounds.getOffset() != RowBounds.NO_ROW_OFFSET) {
                rs.absolute(rowBounds.getOffset());
            }
        } else {
            for (int i = 0; i < rowBounds.getOffset(); i++) {
                rs.next();
            }
        }
    }

    private boolean shouldProcessMoreRows(ResultContext context, RowBounds rowBounds) throws SQLException {
        return !context.isStopped() && context.getResultCount() < rowBounds.getLimit();
    }

    private void handleRowValuesForSimpleResultMap(ResultSetWrapper rsw, ResultMap resultMap, ResultProcessor resultHandler, RowBounds rowBounds, ResultMapping parentMapping)
            throws SQLException {
        DefaultResultContext resultContext = new DefaultResultContext();
        skipRows(rsw.getResultSet(), rowBounds);
    }

    public ResultMap resolveDiscriminatedResultMap(ResultSet rs, ResultMap resultMap, String columnPrefix) throws SQLException {
        Set<String> pastDiscriminators = new HashSet<String>();
        Discriminator discriminator = resultMap.getDiscriminator();
        while (discriminator != null) {
            final Object value = getDiscriminatorValue(rs, discriminator, columnPrefix);
            final String discriminatedMapId = discriminator.getMapIdFor(String.valueOf(value));
            if (configuration.hasResultMap(discriminatedMapId)) {
                resultMap = configuration.getResultMap(discriminatedMapId);
                Discriminator lastDiscriminator = discriminator;
                discriminator = resultMap.getDiscriminator();
                if (discriminator == lastDiscriminator || !pastDiscriminators.add(discriminatedMapId)) {
                    break;
                }
            } else {
                break;
            }
        }
        return resultMap;
    }

    private Object getDiscriminatorValue(ResultSet rs, Discriminator discriminator, String columnPrefix) throws SQLException {
        final ResultMapping resultMapping = discriminator.getResultMapping();
        final TypeHandler<?> typeHandler = resultMapping.getTypeHandler();
        return typeHandler.getResult(rs, prependPrefix(resultMapping.getColumn(), columnPrefix));
    }

    private String prependPrefix(String columnName, String prefix) {
        if (columnName == null || columnName.length() == 0 || prefix == null || prefix.length() == 0) {
            return columnName;
        }
        return prefix + columnName;
    }

    private ResultSetWrapper getNextResultSet(Statement stmt) throws SQLException {
        try {
            if (stmt.getConnection().getMetaData().supportsMultipleResultSets()) {
                if (!((!stmt.getMoreResults()) && (stmt.getUpdateCount() == -1))) {
                    ResultSet rs = stmt.getResultSet();
                    return rs != null ? new ResultSetWrapper(rs, configuration) : null;
                }
            }
        } catch (Exception e) {
            // Intentionally ignored.
        }
        return null;
    }

    private void cleanUpAfterHandlingResultSet() {
        ancestorColumnPrefix.clear();
    }

    @SuppressWarnings("unchecked")
    private List<Object> collapseSingleResultList(List<Object> multipleResults) {
        return multipleResults.size() == 1 ? (List<Object>) multipleResults.get(0) : multipleResults;
    }
}
