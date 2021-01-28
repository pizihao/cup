package com.qlu.cup.context;

import com.qlu.cup.bind.BindException;
import com.qlu.cup.builder.yml.YNode;
import com.qlu.cup.transaction.TransactionFactory;
import com.qlu.cup.util.PartsUtil;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @program: cup
 * @description: 环境,加载的环境
 * @author: liuwenhao
 * @create: 2021-01-25 14:36
 **/
public final class Environment {
  //环境id
  private final String id;
  //事务工厂
  private final TransactionFactory transactionFactory;
  //数据源
  private final DataSource dataSource;
  //映射文件
  private final String mapperPath;
  //class<?> 表示存储的命名空间的  类对象，这个是找映射的关键
  private final Map<Class<?>, YNode> yNodeMap;

  public Environment(String id, TransactionFactory transactionFactory, DataSource dataSource, String mapperPath,Map<Class<?>, YNode> yNodeMap) {
    if (id == null) {
      throw new IllegalArgumentException("Parameter 'id' must not be null");
    }
    if (transactionFactory == null) {
        throw new IllegalArgumentException("Parameter 'transactionFactory' must not be null");
    }
    this.id = id;
    if (dataSource == null) {
      throw new IllegalArgumentException("Parameter 'dataSource' must not be null");
    }
    if (mapperPath == null){
      mapperPath = PartsUtil.MAPPER_PATH;
    }
    this.transactionFactory = transactionFactory;
    this.dataSource = dataSource;
    this.mapperPath = mapperPath;
    this.yNodeMap = yNodeMap;
  }

    public boolean hasNode(Class<?> forName, String statementName) {
      return yNodeMap.containsKey(forName) && yNodeMap.get(forName).getNode().containsKey(statementName);
    }

    //建造模式
  public static class Builder {
      private String id;
      private TransactionFactory transactionFactory;
      private DataSource dataSource;
      private String mapperPath;
      private Map<Class<?>, YNode> yNodeMap;

    public Builder(String id) {
      this.id = id;
    }

    public Builder transactionFactory(TransactionFactory transactionFactory) {
      this.transactionFactory = transactionFactory;
      return this;
    }

    public Builder dataSource(DataSource dataSource) {
      this.dataSource = dataSource;
      return this;
    }

    public Builder mapperPath(String mapperPath) {
      this.mapperPath = mapperPath;
      return this;
    }

    public Builder yNodeMap(Map<Class<?>, YNode> yNodeMap) {
      this.yNodeMap = yNodeMap;
      return this;
    }

    public String id() {
      return this.id;
    }

    public Environment build() {
      return new Environment(this.id, this.transactionFactory, this.dataSource,this.mapperPath,this.yNodeMap);
    }

  }

  public String getId() {
    return this.id;
  }

  public TransactionFactory getTransactionFactory() {
    return this.transactionFactory;
  }

  public DataSource getDataSource() {
    return this.dataSource;
  }

  public String getMapperPath() {
    return mapperPath;
  }

  public Map<Class<?>, YNode> getyNodeMap() {
    return yNodeMap;
  }
}
