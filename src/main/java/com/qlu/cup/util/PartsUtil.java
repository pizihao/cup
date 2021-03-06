package com.qlu.cup.util;

/**
 * @program: cup
 * @description: 部分常量
 * @author: liuwenhao
 * @create: 2021-01-26 17:35
 **/
public interface PartsUtil {
    /**
     * 配置文件
     */
    String NAME = "cup";
    String DATA_SOURCE = "datasource";
    String ENVIRONMENT = "environment";
    String MAPPER_PATH_NAME = "mapperpath";
    String MAPPER_PATH = "classpath:mapper/*.yml";
    String LOG = "log";
    String LOG_NAME = "true";
    /**
     * 映射文件
     */
    String MAPPER = "mapper";
    String NAME_SPACE = "namespace";
    String ID = "id";
    String SQL = "sql";
    String RESULT_TYPE = "resultType";
    String PARAMETER_TYPE = "parameterType";
    String GENERATED_KEY = "generatedKey";
    String GENERATED_KEY_NAME = "false";
    String CACHE = "cache";
    String CACHE_NAME = "true";
    /**
     * 操作
     */
    String INSERT = "insert";
    String SELECT = "select";
    String DELETE = "delete";
    String UPDATE = "update";
    /**
     * Statement
     */
    int TIMEOUT = 5000;
    int FETCH_SIZE = 50;
    /**
     * sql类型
     */
    String SQL_TYPE = "sqlType";
    String SQL_TYPE_NAME = "DML";
}