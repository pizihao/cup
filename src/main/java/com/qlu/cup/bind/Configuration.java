package com.qlu.cup.bind;

import com.qlu.cup.builder.yml.YNode;
import com.qlu.cup.context.Environment;
import com.qlu.cup.session.SqlSession;

/**
 * @program: cup
 * @description: 通过配置文件实现接口和映射文件的绑定，产出一个sqlSession
 * @author: liuwenhao
 * @create: 2021-01-28 11:44
 **/
public class Configuration {

    protected Environment environment;

    /**
     * @description: 使用SQLSession创建一个mapper接口的代理，使用反射，通过这个代理去执行接口的方法
     * @param type 接口
     * @param session 当前的sqlSession
     * @return T
     * @author liuwenaho
     * @date 2021/1/28 14:07
     */
    public <T> T getMapper(Class<T> type, SqlSession session){
        return null;
    }

    /**
     * @description: 获取
     * @param statement YNode的nameSpace + name，代表执行的操作
     * @return com.qlu.cup.builder.yml.YNode
     * @author liuwenaho
     * @date 2021/1/28 14:15
     */
    public YNode getMappedYnode(String statement) {
        return null;
    }

    /**
     * @description: 获得当前的环境
     * @return com.qlu.cup.context.Environment
     * @author liuwenaho
     * @date 2021/1/28 14:19
     */
    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}