package com.qlu.cup.session;

import com.qlu.cup.bind.BindException;
import com.qlu.cup.bind.Configuration;
import com.qlu.cup.bind.ErrorContext;
import com.qlu.cup.exception.CupException;
import com.qlu.cup.executor.Executor;
import com.qlu.cup.mapper.BoundSql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * @program: cup
 * @description: 默认SqlSession
 * @author: liuwenhao
 * @create: 2021-01-27 22:01
 **/
public class DefSqlSession implements SqlSession {


    private final Configuration configuration;
    private final Executor executor;
    private final boolean autoCommit;
    private boolean dirty;

    public DefSqlSession(Configuration configuration, Executor executor, boolean autoCommit) {
        this.configuration = configuration;
        this.executor = executor;
        this.dirty = false;
        this.autoCommit = autoCommit;
    }

    public DefSqlSession(Configuration configuration, Executor executor) {
        this(configuration, executor, false);
    }

    @Override
    public <T> T select(String statement, Object parameter) {
        // Popular vote was to return null on 0 results and throw exception on too many.
        List<T> list = this.selectList(statement, parameter);
        if (list.size() == 1) {
            return list.get(0);
        } else if (list.size() > 1) {
            throw new CupException("映射错误，返回了多个结果: " + list.size());
        } else {
            return null;
        }
    }

    @Override
    public <E> List<E> selectList(String statement, Object parameter) {
        try {
            BoundSql boundSql = configuration.getMappedYnode(statement);
            return executor.query(boundSql, wrapCollection(parameter));
        } catch (Exception e) {
            throw new SqlSessionException("数据库发生错误: " + e, e);
        } finally {
            ErrorContext.instance().reset();
        }
    }

    @Override
    public int insert(String statement, Object parameter) {
        return update(statement, parameter);
    }

    @Override
    public int update(String statement, Object parameter) {
        try {
            dirty = true;
            BoundSql boundSql = configuration.getMappedYnode(statement);
            return executor.update(boundSql, wrapCollection(parameter));
        } catch (Exception e) {
            throw new SqlSessionException("数据库发生错误: " + e, e);
        } finally {
            ErrorContext.instance().reset();
        }
    }


    @Override
    public int delete(String statement, Object parameter) {
        return update(statement, parameter);
    }

    @Override
    public void commit() {
        try {
            executor.commit(isCommitOrRollbackRequired(false));
            dirty = false;
        } catch (Exception e) {
            throw new SqlSessionException("数据库发生错误: " + e, e);
        } finally {
            ErrorContext.instance().reset();
        }
    }

    @Override
    public void rollback() {
        try {
            executor.rollback(isCommitOrRollbackRequired(false));
            dirty = false;
        } catch (Exception e) {
            throw new SqlSessionException("数据库发生错误: " + e, e);
        } finally {
            ErrorContext.instance().reset();
        }
    }

    @Override
    public void close() {
        try {
            executor.close(isCommitOrRollbackRequired(false));
            dirty = false;
        } finally {
            ErrorContext.instance().reset();
        }
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type, this);
    }

    @Override
    public Connection getConnection() {
        try {
            return executor.getTransaction().getConnection();
        } catch (SQLException e) {
            throw new SqlSessionException("连接错误: " + e, e);
        }
    }

    private boolean isCommitOrRollbackRequired(boolean force) {
        return (!autoCommit && dirty) || force;
    }
    private Object wrapCollection(final Object object) {
        if (object instanceof Collection) {
            StrictMap<Object> map = new StrictMap<>();
            map.put("collection", object);
            if (object instanceof List) {
                map.put("list", object);
            }
            return map;
        } else if (object != null && object.getClass().isArray()) {
            StrictMap<Object> map = new StrictMap<>();
            map.put("array", object);
            return map;
        }
        return object;
    }

    public static class StrictMap<V> extends HashMap<String, V> {

        private static final long serialVersionUID = -5741767162221585340L;

        @Override
        public V get(Object key) {
            if (!super.containsKey(key)) {
                throw new BindException("参数 '" + key + "' 没有找到. 可用的参数 " + this.keySet());
            }
            return super.get(key);
        }

    }
}