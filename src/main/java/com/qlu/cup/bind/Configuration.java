package com.qlu.cup.bind;

import com.qlu.cup.builder.yml.YNode;
import com.qlu.cup.context.Environment;
import com.qlu.cup.executor.Executor;
import com.qlu.cup.mapper.BoundSql;
import com.qlu.cup.mapper.BoundSqlBuilder;
import com.qlu.cup.session.SqlSession;
import com.qlu.cup.transaction.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: cup
 * @description: 通过配置文件实现接口和映射文件的绑定，产出一个sqlSession
 * @author: liuwenhao
 * @create: 2021-01-28 11:44
 **/
public class Configuration {

    protected Environment environment;

    protected Map<String,BoundSql> sqlMap = new HashMap<>(16);

    /**
     * @param type    接口
     * @param session 当前的sqlSession
     * @return T
     * @description: 使用SQLSession创建一个mapper接口的代理，使用反射，通过这个代理去执行接口的方法
     * @author liuwenaho
     * @date 2021/1/28 14:07
     */
    public <T> T getMapper(Class<T> type, SqlSession session) {
        YNode yNode = getEnvironment().getyNodeMap().get(type);
        sqlMap = BoundSqlBuilder.builder(yNode.getNode());
        return null;
    }

    /**
     * @param statement YNode的nameSpace + name，代表执行的方法
     * @return com.qlu.cup.builder.yml.YNode
     * @description: 获取statement对应的数据信息
     * @author liuwenaho
     * @date 2021/1/28 14:15
     */
    public BoundSql getMappedYnode(String statement) {
        if (!sqlMap.containsKey(statement)){
            throw new BindException("接口和映射文件绑定失败");
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

    public Map<String, BoundSql> getSqlMap() {
        return sqlMap;
    }

    public void setSqlMap(Map<String, BoundSql> sqlMap) {
        this.sqlMap = sqlMap;
    }

    public Configuration(Environment environment){
        this.environment = environment;
    }
    public boolean hasNode(String nameSpace, String statementName) {
        boolean hasnode = false;
        try {
            hasnode = environment.hasNode(Class.forName(nameSpace), statementName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return hasnode;
    }

    public Executor newExecutor(Transaction tx) {
        return null;
    }
}