package com.qlu.cup.bind;

import com.qlu.cup.builder.yml.MapperException;
import com.qlu.cup.builder.yml.YNode;
import com.qlu.cup.builder.yml.YmlMapperRead;
import com.qlu.cup.context.Environment;
import com.qlu.cup.executor.Executor;
import com.qlu.cup.mapper.*;
import com.qlu.cup.reflection.factory.DefaultObjectFactory;
import com.qlu.cup.result.DefaultResultSetHandler;
import com.qlu.cup.result.ResultProcessor;
import com.qlu.cup.result.ResultSetHandler;
import com.qlu.cup.session.RowBounds;
import com.qlu.cup.session.SqlSession;
import com.qlu.cup.transaction.Transaction;
import com.qlu.cup.type.JdbcType;
import com.qlu.cup.type.TypeHandlerRegistry;
import com.qlu.cup.util.InterceptorChain;
import com.qlu.cup.reflection.MetaObject;
import com.qlu.cup.reflection.factory.ObjectFactory;
import com.qlu.cup.reflection.wrapper.DefaultObjectWrapperFactory;
import com.qlu.cup.reflection.wrapper.ObjectWrapperFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: cup
 * @description: 通过配置文件实现接口和映射文件的绑定，产出一个sqlSession
 * @author: liuwenhao
 * @create: 2021-01-28 11:44
 **/
public class Configuration {

    private static Configuration configuration;

    protected Environment environment;

    private TypeHandlerRegistry typeHandlerRegistry;

    protected static Map<String, BoundSql> sqlMap = new HashMap<>(16);

    private static Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();

    protected final InterceptorChain interceptorChain;

    protected JdbcType jdbcTypeForNull = JdbcType.OTHER;
    protected ObjectFactory objectFactory = new DefaultObjectFactory();
    protected ObjectWrapperFactory objectWrapperFactory = new DefaultObjectWrapperFactory();

    /**
     * @param type       接口
     * @param sqlSession 当前的sqlSession
     * @return T
     * @description: 使用SQLSession创建一个mapper接口的代理，使用反射，通过这个代理去执行接口的方法
     * @author liuwenaho
     * @date 2021/1/28 14:07
     */
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        if (!environment.getyNodeMap().containsKey(type)) {
            throw new MapperException("找不到映射信息" + type);
        }
        final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
        if (mapperProxyFactory == null) {
            throw new BindException("找不到映射信息" + type);
        }
        try {
            return mapperProxyFactory.newInstance(sqlSession);
        } catch (Exception e) {
            throw new BindException("Error getting mapper instance. Cause: " + e, e);
        }
    }

    /**
     * @param statement YNode的nameSpace + name，代表执行的方法
     * @return com.qlu.cup.builder.yml.YNode
     * @description: 获取statement对应的数据信息
     * @author liuwenaho
     * @date 2021/1/28 14:15
     */
    public BoundSql getMappedYnode(String statement) {
        if (!sqlMap.containsKey(statement)) {
            throw new BindException("找不到映射信息" + statement);
        }
        return sqlMap.get(statement);
    }

    /**
     * @return com.qlu.cup.context.Environment
     * @description: 获得当前的环境
     * @author liuwenaho
     * @date 2021/1/28 14:19
     */
    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Configuration(Environment environment) {
        this.environment = environment;
        this.typeHandlerRegistry = new TypeHandlerRegistry();
        this.interceptorChain = new InterceptorChain();
    }

    public static Configuration getConfiguration(Environment environment) {
        if (configuration == null) {
            configuration = new Configuration(environment);
        }
        if (configuration.getSqlMap().isEmpty() && configuration.getKnownMappers().isEmpty()) {
            for (Map.Entry<Class<?>, YNode> entry : environment.getyNodeMap().entrySet()) {
                if (YmlMapperRead.checkOverload(entry.getKey())) {
                    throw new BindException("禁止在" + entry.getKey() + "中出现方法重载");
                }
                sqlMap.putAll(BoundSqlBuilder.builder(entry.getValue().getNode(), configuration));
                knownMappers.put(entry.getKey(), new MapperProxyFactory<>(entry.getKey()));
            }
        }
        return configuration;
    }

    public Map<String, BoundSql> getSqlMap() {
        return sqlMap;
    }

    public boolean hasNode(String nameSpace, String statementName) {
        boolean hasNode = false;
        try {
            hasNode = environment.hasNode(Class.forName(nameSpace), statementName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return hasNode;
    }

    public Executor newExecutor(Transaction tx) {
        return null;
    }

    public TypeHandlerRegistry getTypeHandlerRegistry() {
        return typeHandlerRegistry;
    }

    public Map<Class<?>, MapperProxyFactory<?>> getKnownMappers() {
        return knownMappers;
    }

    public InterceptorChain getInterceptorChain() {
        return interceptorChain;
    }

    //创建参数处理器
    public ParameterHandler newParameterHandler(Object parameterObject, BoundSql boundSql) {
        //创建ParameterHandler
        ParameterHandler parameterHandler = new DefaultParameterHandler(parameterObject, boundSql);
        //插件在这里插入
        parameterHandler = (ParameterHandler) interceptorChain.pluginAll(parameterHandler);
        return parameterHandler;
    }

    public ResultSetHandler newResultSetHandler(Executor executor, RowBounds rowBounds, ParameterHandler parameterHandler,
                                                ResultProcessor resultHandler, BoundSql boundSql) {
        ResultSetHandler resultSetHandler = new DefaultResultSetHandler(executor, parameterHandler, resultHandler, boundSql, rowBounds);
        resultSetHandler = (ResultSetHandler) interceptorChain.pluginAll(resultSetHandler);
        return resultSetHandler;
    }

    public MetaObject newMetaObject(Object parameterObject) {
        return MetaObject.forObject(parameterObject, objectFactory, objectWrapperFactory);
    }

    public JdbcType getJdbcTypeForNull() {
        return jdbcTypeForNull;
    }
}