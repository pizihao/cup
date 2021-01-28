package com.qlu.cup.session;

import com.qlu.cup.bind.Configuration;
import com.qlu.cup.builder.yml.YNode;
import com.qlu.cup.builder.yml.YmlMapperRead;
import com.qlu.cup.conf.InputConf;
import com.qlu.cup.context.Environment;
import com.qlu.cup.context.ErrorContext;
import com.qlu.cup.datasource.DataSourceFactory;
import com.qlu.cup.datasource.DefDataSourceFactory;
import com.qlu.cup.exception.ExceptionFactory;
import com.qlu.cup.transaction.DefJdbcTransactionFactory;
import com.qlu.cup.transaction.TransactionFactory;
import com.qlu.cup.util.PartsUtil;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;
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
            throw ExceptionFactory.wrapException("Error building SqlSession.", e);
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
            throw ExceptionFactory.wrapException("Error building SqlSession.", e);
        } finally {
            ErrorContext.instance().reset();
            try {
                inputStream.close();
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * 用于创建事务管理器和数据源，并存储在环境中
     */
    public Configuration txAndDs(Properties properties) {
        try {
            //事务管理器
            TransactionFactory txFactory = new DefJdbcTransactionFactory();
            txFactory.setProperties(properties);
            //数据源
            DataSourceFactory dsFactory = new DefDataSourceFactory();
            dsFactory.setProperties(properties);
            DataSource dataSource = dsFactory.getDataSource();
            String mapperPath = properties.getProperty(PartsUtil.MAPPER_PATH_NAME);
            //按照mapperPath路径，读取全部的映射文件，放入环境中
            Map<Class<?>, YNode> mapper = YmlMapperRead.getMapper(mapperPath);
            Environment.Builder environmentBuilder = new Environment.Builder(properties.getProperty(PartsUtil.ENVIRONMENT))
                    .transactionFactory(txFactory)
                    .dataSource(dataSource)
                    .mapperPath(mapperPath)
                    .yNodeMap(mapper);
            Environment build = environmentBuilder.build();

            //创建Configuration

            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public SqlSessionFactory build(Configuration configuration) {
        return new DefSqlSessionFactory(configuration);
    }
}