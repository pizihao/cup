package com.qlu.cup.bind;

import com.qlu.cup.builder.yml.MapperException;
import com.qlu.cup.builder.yml.YNode;
import com.qlu.cup.context.Environment;
import com.qlu.cup.executor.Executor;
import com.qlu.cup.mapper.BoundSql;
import com.qlu.cup.mapper.BoundSqlBuilder;
import com.qlu.cup.mapper.MapperProxyFactory;
import com.qlu.cup.session.SqlSession;
import com.qlu.cup.transaction.Transaction;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: cup
 * @description: 通过配置文件实现接口和映射文件的绑定，产出一个sqlSession
 * @author: liuwenhao
 * @create: 2021-01-28 11:44
 **/
public class Configuration {

    protected Environment environment;

    protected static Map<String, BoundSql> sqlMap = new HashMap<>(16);

    private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<Class<?>, MapperProxyFactory<?>>();

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

        for (Map.Entry<Class<?>, YNode> entry : environment.getyNodeMap().entrySet()) {
            sqlMap.putAll(BoundSqlBuilder.builder(entry.getValue().getNode()));
            knownMappers.put(entry.getKey(), new MapperProxyFactory<>(entry.getKey()));
        }
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
}