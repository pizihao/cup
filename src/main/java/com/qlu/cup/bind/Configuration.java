package com.qlu.cup.bind;

import com.qlu.cup.builder.yml.MapperException;
import com.qlu.cup.cache.Cacheable;
import com.qlu.cup.cache.CupCache;
import com.qlu.cup.cache.NoCache;
import com.qlu.cup.executor.CupExecutor;
import com.qlu.cup.executor.Executor;
import com.qlu.cup.mapper.BoundSql;
import com.qlu.cup.mapper.MapperProxyFactory;
import com.qlu.cup.result.ResultType;
import com.qlu.cup.session.SqlSession;
import com.qlu.cup.transaction.Transaction;
import com.qlu.cup.util.PartsUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: cup
 * @description: 全局配置信息
 * @author: liuwenhao
 * @create: 2021-03-03 13:16
 **/
public class Configuration {
    private static Configuration configuration;

    protected Environment environment;

    /**
     * key为命名空间+接口方法名
     **/
    protected static Map<String, BoundSql> sqlMap = new HashMap<>(16);
    /**
     * key为命名空间+接口方法名
     **/
    protected static Map<String, ResultType> resultMap = new HashMap<>(16);

    private static Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();

    private static Cacheable cupCache;

    /**
     * @param type       接口
     * @param sqlSession 当前的sqlSession
     * @return T
     * @description: 使用SQLSession创建一个mapper接口的代理，使用反射，通过这个代理去执行接口的方法
     * @author liuwenaho
     * @date 2021/1/28 14:07
     */
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        if (!getSqlMap(type)) {
            throw new MapperException("找不到映射信息" + type);
        }
        final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
        if (mapperProxyFactory == null) {
            throw new BindException("找不到映射信息" + type);
        }
        try {
            return mapperProxyFactory.newInstance(sqlSession);
        } catch (Exception e) {
            throw new BindException("找不到映射信息: " + e, e);
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
    }

    public static Configuration getConfiguration() {
        return configuration;
    }
    public Map<String, BoundSql> getSqlMap() {
        return sqlMap;
    }
    public boolean getSqlMap(Class<?> type) {
        for (Map.Entry<String, BoundSql> entry : sqlMap.entrySet()) {
            if (entry.getValue().getNamespace().equals(type.getName())){
                return true;
            }
        }
        return false;
    }

    public boolean hasNode(String statementName) {
        return sqlMap.containsKey(statementName);
    }

    /**
     * 创建一个执行器并返回
     **/
    public Executor newExecutor(Transaction tx) {
        return new CupExecutor(environment, tx);
    }

    public void setSqlMap(Map<String, BoundSql> sqlMap) {
        Configuration.sqlMap = sqlMap;
    }

    public Map<String, ResultType> getResultMap() {
        return resultMap;
    }

    public void setResultMap(String name, ResultType resultMap) {
        Configuration.resultMap.put(name, resultMap);
    }

    public Map<Class<?>, MapperProxyFactory<?>> getKnownMappers() {
        return knownMappers;
    }

    public void setKnownMappers(Class<?> clazz, MapperProxyFactory knownMappers) {
        Configuration.knownMappers.put(clazz,knownMappers);
    }

    public Boolean isLog(){
        return environment.getLog();
    }

    public Cacheable getCupCache() {
        if (environment.getCache()){
            cupCache = new CupCache();
        }else{
            cupCache = new NoCache();
        }
        return cupCache;
    }
}