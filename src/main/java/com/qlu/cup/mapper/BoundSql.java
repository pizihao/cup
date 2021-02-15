package com.qlu.cup.mapper;

import com.qlu.cup.bind.Configuration;
import com.qlu.cup.builder.yml.MapperException;
import com.qlu.cup.context.Environment;
import com.qlu.cup.logging.Log;
import com.qlu.cup.logging.LogFactory;
import com.qlu.cup.result.ResultMap;
import com.qlu.cup.statement.ParameterMap;
import com.qlu.cup.statement.ParameterMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * SQL，参数和命名空间+id进行绑定
 */
public class BoundSql {

    /**
     * 对应的操作
     */
    private String handle;

    /**
     * 未经过处理的sql
     */
    private String sql;

    /**
     * 参数类型 这个数如果是null就代表着需要使用接口中传入的参数进行获取
     */
    private Class<?> parameterType;

    /**
     * 返回值类型
     */
    private Class<?> resultType;

    /**
     * 命名空间+id
     */
    private String ID;

    /**
     * 环境，包括数据库连接，事务工厂等
     */
    private Environment environment;

    /**
     * 参数映射，包含所有的参数
     */
    private List<ParameterMapping> parameterMappings;

    /**
     * 参数映射，里面有这个SQL语句需要的所有参数
     */
    private ParameterMap parameterMap;

    /**
     * 结果映射，和SQL绑定的SQL必须保持一致
     */
    private List<ResultMap> resultMaps;

    /**
     * 用于获取sql
     */
    private SqlSource sqlSource;

    /**
     * 日志Id
     */
    private Log statementLog;

    public BoundSql(String handle, String sql, String parameterType, String resultType, String ID,
                    Environment environment, SqlSource sqlSource) {
        this.handle = handle;
        this.ID = ID;
        this.parameterType = ObjectInterface.getClazz(parameterType);
        this.resultType = ObjectInterface.getClazz(resultType);
        this.sql = sql;
        this.environment = environment;
        this.sqlSource = sqlSource;
        this.parameterMap = new ParameterMap.Builder("defaultParameterMap", null, new ArrayList<ParameterMapping>()).build();
        this.resultMaps = new ArrayList<ResultMap>();
        this.statementLog = LogFactory.getLog(ID);
    }

    public static BoundSql getBoundSql(Configuration configuration, String sql, List<ParameterMapping> parameterMappings, Object parameterObject) {
        Map<String, BoundSql> sqlMap = Configuration.getConfiguration(configuration.getEnvironment()).getSqlMap();
        AtomicReference<BoundSql> hasSql = new AtomicReference<>();
        AtomicBoolean count = new AtomicBoolean(false);
        sqlMap.forEach((s, boundSql) -> {
            if (boundSql.sql.equals(sql)) {
                count.set(true);
                hasSql.set(boundSql);
            }
        });
        if (count.get()) {
            throw new MapperException("没有找到对应的SQL" + sql);
        }
        return hasSql.get();
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Class<?> getParameterType() {
        return parameterType;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public void setParameterType(Class<?> parameterType) {
        this.parameterType = parameterType;
    }

    public Class<?> getResultType() {
        return resultType;
    }

    public void setResultType(Class<?> resultType) {
        this.resultType = resultType;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public SqlSource getSqlSource() {
        return sqlSource;
    }

    public ParameterMap getParameterMap() {
        return parameterMap;
    }

    public List<ResultMap> getResultMaps() {
        return resultMaps;
    }

    public BoundSql getBoundSql(Object parameterObject) {
        return this;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public List<ParameterMapping> getParameterMappings() {
        return parameterMappings;
    }

    public void setParameterMappings(List<ParameterMapping> parameterMappings) {
        this.parameterMappings = parameterMappings;
    }

    public void setParameterMap(ParameterMap parameterMap) {
        this.parameterMap = parameterMap;
    }

    public void setResultMaps(List<ResultMap> resultMaps) {
        this.resultMaps = resultMaps;
    }

    public void setSqlSource(SqlSource sqlSource) {
        this.sqlSource = sqlSource;
    }

    public Log getStatementLog() {
        return statementLog;
    }

    public void setStatementLog(Log statementLog) {
        this.statementLog = statementLog;
    }

    private static String[] delimitedStringtoArray(String in) {
        if (in == null || in.trim().length() == 0) {
            return null;
        } else {
            return in.split(",");
        }
    }
}
