package com.qlu.cup.session;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import com.qlu.cup.bind.Configuration;
import com.qlu.cup.context.Environment;
import com.qlu.cup.result.ResultProcessor;
import com.qlu.cup.transaction.TransactionIsolationLevel;
import com.qlu.cup.util.ExceptionUtil;

/**
 * SqlSession管理员
 */
public class SqlSessionManager implements SqlSessionFactory, SqlSession {

    private final SqlSessionFactory sqlSessionFactory;
    private final SqlSession sqlSessionProxy;

    private ThreadLocal<SqlSession> localSqlSession = new ThreadLocal<SqlSession>();

    private SqlSessionManager(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.sqlSessionProxy = (SqlSession) Proxy.newProxyInstance(
                SqlSessionFactory.class.getClassLoader(),
                new Class[]{SqlSession.class},
                new SqlSessionInterceptor());
    }

    public static SqlSessionManager newInstance(Reader reader) {
        return new SqlSessionManager(new SqlSessionFactoryBuilder().build(reader));
    }

    public static SqlSessionManager newInstance(InputStream inputStream) {
        return new SqlSessionManager(new SqlSessionFactoryBuilder().build(inputStream));
    }

    public static SqlSessionManager newInstance(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionManager(sqlSessionFactory);
    }

    public void startManagedSession() {
        this.localSqlSession.set(getSession());
    }

    public void startManagedSession(boolean autoCommit) {
        this.localSqlSession.set(getSession(autoCommit));
    }

    public void startManagedSession(Connection connection) {
        this.localSqlSession.set(getSession(connection));
    }

    public void startManagedSession(TransactionIsolationLevel level) {
        this.localSqlSession.set(getSession(level));
    }

    public boolean isManagedSessionStarted() {
        return this.localSqlSession.get() != null;
    }

    @Override
    public SqlSession getSession() {
        return sqlSessionFactory.getSession();
    }

    @Override
    public SqlSession getSession(boolean autoCommit) {
        return sqlSessionFactory.getSession(autoCommit);
    }

    @Override
    public SqlSession getSession(Connection connection) {
        return sqlSessionFactory.getSession(connection);
    }

    @Override
    public SqlSession getSession(TransactionIsolationLevel level) {
        return sqlSessionFactory.getSession(level);
    }

    @Override
    public <T> T selectOne(String statement) {
        return sqlSessionProxy.<T>selectOne(statement);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        return sqlSessionProxy.<T>selectOne(statement, parameter);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
        return sqlSessionProxy.<K, V>selectMap(statement, mapKey);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey) {
        return sqlSessionProxy.<K, V>selectMap(statement, parameter, mapKey);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds) {
        return sqlSessionProxy.<K, V>selectMap(statement, parameter, mapKey, rowBounds);
    }

    @Override
    public <E> List<E> selectList(String statement) {
        return sqlSessionProxy.<E>selectList(statement);
    }

    @Override
    public <E> List<E> selectList(String statement, Object parameter) {
        return sqlSessionProxy.<E>selectList(statement, parameter);
    }

    @Override
    public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
        return sqlSessionProxy.<E>selectList(statement, parameter, rowBounds);
    }

    @Override
    public void select(String statement, ResultProcessor handler) {
        sqlSessionProxy.select(statement, handler);
    }

    @Override
    public void select(String statement, Object parameter, ResultProcessor handler) {
        sqlSessionProxy.select(statement, parameter, handler);
    }

    @Override
    public void select(String statement, Object parameter, RowBounds rowBounds, ResultProcessor handler) {
        sqlSessionProxy.select(statement, parameter, rowBounds, handler);
    }

    @Override
    public int insert(String statement, Object parameter) {
        return sqlSessionProxy.insert(statement, parameter);
    }

    @Override
    public int update(String statement) {
        return sqlSessionProxy.update(statement);
    }

    @Override
    public int update(String statement, Object parameter) {
        return sqlSessionProxy.update(statement, parameter);
    }

    @Override
    public int delete(String statement) {
        return sqlSessionProxy.delete(statement);
    }

    @Override
    public int delete(String statement, Object parameter) {
        return sqlSessionProxy.delete(statement, parameter);
    }

    @Override
    public void clearCache() {
        final SqlSession sqlSession = localSqlSession.get();
        if (sqlSession == null) {
            throw new SqlSessionException("Error:  Cannot clear the cache.  No managed session is started.");
        }
        sqlSession.clearCache();
    }

    @Override
    public void commit() {
        final SqlSession sqlSession = localSqlSession.get();
        if (sqlSession == null) {
            throw new SqlSessionException("Error:  Cannot commit.  No managed session is started.");
        }
        sqlSession.commit();
    }

    @Override
    public void commit(boolean force) {
        final SqlSession sqlSession = localSqlSession.get();
        if (sqlSession == null) {
            throw new SqlSessionException("Error:  Cannot commit.  No managed session is started.");
        }
        sqlSession.commit(force);
    }

    @Override
    public void rollback() {
        final SqlSession sqlSession = localSqlSession.get();
        if (sqlSession == null) {
            throw new SqlSessionException("Error:  Cannot rollback.  No managed session is started.");
        }
        sqlSession.rollback();
    }

    @Override
    public void rollback(boolean force) {
        final SqlSession sqlSession = localSqlSession.get();
        if (sqlSession == null) {
            throw new SqlSessionException("Error:  Cannot rollback.  No managed session is started.");
        }
        sqlSession.rollback(force);
    }

    @Override
    public Connection getConnection() {
        final SqlSession sqlSession = localSqlSession.get();
        if (sqlSession == null) {
            throw new SqlSessionException("Error:  Cannot get connection.  No managed session is started.");
        }
        return sqlSession.getConnection();
    }

    @Override
    public Configuration getConfiguration() {
        return sqlSessionFactory.getConfiguration();
    }

    @Override
    public void close() {
        final SqlSession sqlSession = localSqlSession.get();
        if (sqlSession == null) {
            throw new SqlSessionException("Error:  Cannot close.  No managed session is started.");
        }
        try {
            sqlSession.close();
        } finally {
            localSqlSession.set(null);
        }
    }

    public Environment getEnvironment() {
        return sqlSessionFactory.getConfiguration().getEnvironment();
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return getConfiguration().getMapper(type, this);
    }

    //代理模式
    private class SqlSessionInterceptor implements InvocationHandler {
        public SqlSessionInterceptor() {
            // Prevent Synthetic Access
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            final SqlSession sqlSession = SqlSessionManager.this.localSqlSession.get();
            if (sqlSession != null) {
                //如果当前线程已经有SqlSession了，则直接调用
                try {
                    return method.invoke(sqlSession, args);
                } catch (Throwable t) {
                    throw ExceptionUtil.unwrapThrowable(t);
                }
            } else {
                //如果当前线程没有SqlSession，先打开session，再调用,最后提交
                final SqlSession autoSqlSession = getSession();
                try {
                    final Object result = method.invoke(autoSqlSession, args);
                    autoSqlSession.commit();
                    return result;
                } catch (Throwable t) {
                    autoSqlSession.rollback();
                    throw ExceptionUtil.unwrapThrowable(t);
                } finally {
                    autoSqlSession.close();
                }
            }
        }
    }

}