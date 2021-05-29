package com.qlu.cup.session;

import com.qlu.cup.bind.Configuration;
import com.qlu.cup.bind.Environment;
import com.qlu.cup.bind.ErrorContext;
import com.qlu.cup.builder.InputConf;
import com.qlu.cup.builder.yml.YmlMapperRead;
import com.qlu.cup.datasource.DataSourceFactory;
import com.qlu.cup.datasource.DefDataSourceFactory;
import com.qlu.cup.transaction.DefJdbcTransactionFactory;
import com.qlu.cup.transaction.TransactionFactory;
import com.qlu.cup.util.PartsUtil;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

/**
 * @program: cup
 * @description: 创建sqlsession工厂类，
 * @author: liuwenhao
 * @create: 2021-01-26 11:06
 **/
public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(Reader reader) {
        try {
            Properties properties = InputConf.getProperties(reader);
            return build(txAndDs(properties));
        } catch (Exception e) {
            throw new SqlSessionException("Error building SqlSession.", e);
        } finally {
            ErrorContext.instance().reset();
            try {
                reader.close();
            } catch (IOException e) {
                // Intentionally ignore. Prefer previous error.
            }
        }
    }

    public SqlSessionFactory build(InputStream inputStream) {
        try {
            Properties properties = InputConf.getProperties(inputStream);
            return build(txAndDs(properties));
        } catch (Exception e) {
            throw new SqlSessionException("Error building SqlSession.", e);
        } finally {
            ErrorContext.instance().reset();
            try {
                inputStream.close();
            } catch (IOException ignored) {
            }
        }
    }

    public SqlSessionFactory build(Properties properties) {
        try {
            return build(txAndDs(properties));
        } catch (Exception e) {
            throw new SqlSessionException("Error building SqlSession.", e);
        } finally {
            ErrorContext.instance().reset();
        }
    }

    /**
     * 用于创建事务管理器和数据源，并存储在环境中
     */
    public Configuration txAndDs(Properties properties) {
        try {
            if (Configuration.getConfiguration() != null) {
                return Configuration.getConfiguration();
            }
            //事务管理器
            TransactionFactory txFactory = new DefJdbcTransactionFactory();
            txFactory.setProperties(properties);
            //数据源
            DataSourceFactory dsFactory = new DefDataSourceFactory();
            dsFactory.setProperties(properties);
            //日志
            DataSource dataSource = dsFactory.getDataSource();
            String mapperPath = properties.getProperty(PartsUtil.MAPPER_PATH_NAME);
            Boolean log = null;
            if (properties.getProperty(PartsUtil.LOG) != null) {
                log = Boolean.parseBoolean(properties.getProperty(PartsUtil.LOG));
            }
            Boolean cache = null;
            if (properties.getProperty(PartsUtil.CACHE) != null) {
                cache = Boolean.parseBoolean(properties.getProperty(PartsUtil.CACHE));
            }

            Environment.Builder environmentBuilder = new Environment.Builder(properties.getProperty(PartsUtil.ENVIRONMENT))
                    .transactionFactory(txFactory)
                    .dataSource(dataSource)
                    .mapperPath(mapperPath)
                    .log(log)
                    .cache(cache);
            Environment environment = environmentBuilder.build();
            //创建Configuration
            Configuration configuration = new Configuration(environment);
            //按照mapperPath路径，读取全部的映射文件，放入环境中z`
            YmlMapperRead.getMapper(mapperPath, configuration);
            return configuration;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public SqlSessionFactory build(Configuration configuration) {
        return new DefSqlSessionFactory(configuration);
    }
}