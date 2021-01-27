package com.qlu.cup.session;

import com.qlu.cup.conf.InputConf;
import com.qlu.cup.context.Environment;
import com.qlu.cup.context.ErrorContext;
import com.qlu.cup.datasource.DataSourceFactory;
import com.qlu.cup.datasource.DefDataSourceFactory;
import com.qlu.cup.exception.ExceptionFactory;
import com.qlu.cup.transaction.DefJdbcTransactionFactory;
import com.qlu.cup.transaction.TransactionFactory;

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
        return build(reader, new Properties());
    }

    public SqlSessionFactory build(Reader reader, Properties properties) {
        try {
            properties.load(InputConf.getReader(reader));
            txAndDs(properties);
            return build(properties);
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
        return build(inputStream, new Properties());
    }

    public SqlSessionFactory build(InputStream inputStream, Properties properties) {
        try {
            properties.load(InputConf.getInputStream(inputStream));
            txAndDs(properties);
            return build(properties);
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
    public void txAndDs(Properties properties) {
        try {
            //事务管理器
            TransactionFactory txFactory = new DefJdbcTransactionFactory();
            txFactory.setProperties(properties);
            //数据源
            DataSourceFactory dsFactory = new DefDataSourceFactory();
            dsFactory.setProperties(properties);
            DataSource dataSource = dsFactory.getDataSource();
            Environment.Builder environmentBuilder = new Environment.Builder(properties.getProperty("id"))
                    .transactionFactory(txFactory)
                    .dataSource(dataSource);
            environmentBuilder.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SqlSessionFactory build(Properties properties) {
        return new DefSqlSessionFactory(properties);
    }
}