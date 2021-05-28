package com.qlu.cup.bind;

import com.qlu.cup.transaction.TransactionFactory;
import com.qlu.cup.util.PartsUtil;

import javax.sql.DataSource;

/**
 * @program: cup
 * @description: 环境, 加载的环境
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
    //日志
    private final Boolean log;

    public Environment(String id, TransactionFactory transactionFactory, DataSource dataSource, String mapperPath, Boolean log) {
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
        if (mapperPath == null) {
            mapperPath = PartsUtil.MAPPER_PATH;
        }
        if (log == null) {
            log = true;
        }
        this.transactionFactory = transactionFactory;
        this.dataSource = dataSource;
        this.mapperPath = mapperPath;
        this.log = log;
    }

    //建造模式
    public static class Builder {
        private String id;
        private TransactionFactory transactionFactory;
        private DataSource dataSource;
        private String mapperPath;
        private Boolean log;

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

        public Builder log(Boolean log) {
            this.log = log;
            return this;
        }

        public String id() {
            return this.id;
        }

        public Environment build() {
            return new Environment(this.id, this.transactionFactory, this.dataSource, this.mapperPath, this.log);
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

    public Boolean getLog() {
        return log;
    }
}
