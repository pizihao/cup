package com.qlu.cup.jdbc;

import com.qlu.cup.makers.delete.DefaultDelete;
import com.qlu.cup.makers.delete.Delete;
import com.qlu.cup.makers.insert.DefaultInsert;
import com.qlu.cup.makers.insert.Insert;
import com.qlu.cup.makers.query.DefaultQuery;
import com.qlu.cup.makers.query.Query;
import com.qlu.cup.makers.update.DefaultUpdate;
import com.qlu.cup.makers.update.Update;
import com.qlu.cup.makers.where.Where;
import com.qlu.cup.makers.where.Wheres;
import com.qlu.cup.builder.annotations.EntityMapperFactory;
import com.qlu.cup.builder.annotations.EntityTableRowMapper;
import com.qlu.cup.builder.annotations.FieldColumnRowMapper;
import com.qlu.cup.util.ClassUtils;
import com.qlu.cup.util.EntityUtils;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @program: cup
 * @description: 简化操作的jdbcTemplate
 * @author: liuwenhao
 * @create: 2020-12-21 16:13
 **/
@CommonsLog
public class JdbcPlus {

    private JdbcTemplate jdbcTemplate;

    /**
     * 根据条件查询
     *
     * @param entityClass
     * @param wheres
     * @param <T>
     * @return
     */
    final public <T> List<T> selectBy(final Class<T> entityClass, Where... wheres) {
        Query query = new DefaultQuery();
        query.target(entityClass);
        query.where(wheres);
        return selectBy(query);
    }

    /**
     * 根据条件查询一个
     *
     * @param entityClass
     * @param wheres
     * @param <T>
     * @return
     */
    final public <T> T selectOneBy(final Class<T> entityClass, Where... wheres) {
        return DataAccessUtils.requiredSingleResult(selectBy(entityClass, wheres));
    }

    /**
     * 查询所有
     *
     * @param entityClass
     * @param <T>
     * @return
     */
    final public <T> List<T> select(final Class<T> entityClass) {
        Query query = new DefaultQuery();
        query.target(entityClass);
        return selectBy(query);
    }

    /**
     * 根据id查找
     *
     * @param entityClass
     * @param id
     * @param <T>
     * @return
     */
    final public <T> T selectById(final Class<T> entityClass, final Object id) {
        EntityTableRowMapper mapper = EntityMapperFactory.getMapper(entityClass);
        return selectOneBy(entityClass, mapper.getIdName(), id);
    }

    /**
     * 根据某一字段查找数据
     *
     * @param entityClass
     * @param columnName
     * @param columnValue
     * @param <T>
     * @return
     */
    final public <T> List<T> selectBy(
            final Class<T> entityClass,
            final String columnName, final Object columnValue
    ) {
        return selectBy(entityClass, Wheres.equal(columnName, columnValue));
    }

    /**
     * 根据某字段查找数据
     *
     * @param entityClass
     * @param columnName1
     * @param columnValue1
     * @param columnName2
     * @param columnValue2
     * @param <T>
     * @return
     */
    final public <T> List<T> selectBy(
            final Class<T> entityClass,
            final String columnName1, final Object columnValue1,
            final String columnName2, final Object columnValue2
    ) {
        return selectBy(entityClass,
                Wheres.equal(columnName1, columnValue1),
                Wheres.equal(columnName2, columnValue2)
        );
    }

    /**
     * 根据某一字段查找一条数据
     *
     * @param entityClass
     * @param columnName
     * @param columnValue
     * @param <T>
     * @return
     */
    final public <T> T selectOneBy(
            final Class<T> entityClass,
            final String columnName, final Object columnValue
    ) {
        return DataAccessUtils.requiredSingleResult(selectBy(entityClass, columnName, columnValue));
    }

    /**
     * 根据字段查找一条数据
     *
     * @param entityClass
     * @param columnName1
     * @param columnValue1
     * @param columnName2
     * @param columnValue2
     * @param <T>
     * @return
     */
    final public <T> T selectOneBy(
            final Class<T> entityClass,
            final String columnName1, final Object columnValue1,
            final String columnName2, final Object columnValue2
    ) {
        return selectOneBy(entityClass, columnName1, columnValue1, columnName2, columnValue2);
    }

    /**
     * 根据条件查询
     * 查询结果只能是1条
     *
     * @param query
     * @param <T>
     * @return
     */
    final public <T> T selectOneBy(final Query query) {
        List<T> list = selectBy(query);
        if (list.size() == 0) {
            return null;
        }
        return DataAccessUtils.requiredSingleResult(list);
    }

    /**
     * 执行一条sql
     *
     * @param sql
     * @param sqlValues
     * @param objClass
     * @param <T>
     * @return
     */
    final public <T> List<T> selectBySql(final String sql, final Object[] sqlValues, final Class<T> objClass) {
        return jdbcTemplate.query(sql, sqlValues, new FieldColumnRowMapper(objClass));
    }

    /**
     * 执行一条sql
     * @param sql
     * @param objClass
     * @param <T>
     * @return
     */
    final public <T> List<T> selectBySql(final String sql, final Class<T> objClass) {
        return jdbcTemplate.query(sql, new FieldColumnRowMapper(objClass));
    }

    /**
     * 限定字段查询（多个结果）
     *
     * @param entityClass 添加了@Table的Class
     * @param wheres      查询条件
     * @param columnNames 数据库中的字段个名称
     * @return
     */
    final public List<Map<String, Object>> selectColumnForList(final Class entityClass, final List<Where> wheres, final String... columnNames) {
        Query query = new DefaultQuery();
        query.target(entityClass);
        query.addQueryColumns(Arrays.asList(columnNames));
        query.where(wheres);
        return jdbcTemplate.queryForList(query.toSql(), query.getSqlValues());
    }

    /**
     * 限定字段查询（多个结果）
     *
     * @param entityClass 添加了@Table的Class
     * @param wheres      查询条件
     * @param columnNames 数据库中的字段个名称
     * @return
     */
    final public Map<String, Object> selectColumnForMap(final Class entityClass, final List<Where> wheres, final String... columnNames) {
        return DataAccessUtils.requiredSingleResult(selectColumnForList(entityClass, wheres, columnNames));
    }

    /**
     * 根据条件删除数据
     *
     * @param entityClass
     * @param wheres
     * @return
     */
    final public Integer deleteBy(final Class entityClass, Where... wheres) {
        Delete delete = new DefaultDelete();
        delete.target(entityClass);
        delete.where(wheres);
        return deleteBy(delete);
    }

    /**
     * 根据id删除数据
     *
     * @param entityClass
     * @param id
     * @return
     */
    final public Integer deleteById(final Class entityClass, final Object id) {
        EntityTableRowMapper mapper = EntityMapperFactory.getMapper(entityClass);
        return deleteBy(entityClass, mapper.getIdName(), id);
    }

    /**
     * 根据一个字段删除数据
     *
     * @param entityClass
     * @param columnName
     * @param columnValue
     * @return
     */
    final public Integer deleteBy(final Class entityClass, final String columnName, final Object columnValue) {
        return deleteBy(entityClass, Wheres.equal(columnName, columnValue));
    }

    /**
     * 根据字段删除数据
     *
     * @param entityClass
     * @param columnName1
     * @param columnValue1
     * @param columnName2
     * @param columnValue2
     * @return
     */
    final public Integer deleteBy(
            final Class entityClass,
            final String columnName1, final Object columnValue1,
            final String columnName2, final Object columnValue2
    ) {
        return deleteBy(entityClass, Wheres.equal(columnName1, columnValue1), Wheres.equal(columnName2, columnValue2));
    }

    /**
     * 添加一条数据
     *
     * @param entityClass
     * @param entity
     * @param <T>
     * @return
     */
    final public <T> Integer insert(final Class<T> entityClass, final T entity) {
        Insert insert = new DefaultInsert();
        insert.target(entityClass);
        insert.insert(entity);
        return insertBy(insert);
    }

    /**
     * 批量插入
     *
     * @param entityClass
     * @param entities
     * @return
     */
    final public <T> Integer insertBatch(Class<T> entityClass, final List<T> entities) {
        Insert insert = new DefaultInsert();
        insert.target(entityClass);
        for (int i = 0; i < entities.size(); i++) {
            Object entity = entities.get(i);
            insert.insert(entity);
        }
        return insertBy(insert);
    }

    /**
     * 根据id更新数据
     *
     * @param entityClass
     * @param entity
     * @param <T>
     * @return
     */
    final public <T> Integer updateById(final Class<T> entityClass, final T entity) {
        return updateById(entityClass, entity, false);
    }

    /**
     * 根据id 更新数据
     *
     * @param entityClass
     * @param entity
     * @param ignoreNull  是否忽略null
     * @param <T>
     * @return
     */
    final public <T> Integer updateById(final Class<T> entityClass, final T entity, final boolean ignoreNull) {
        EntityTableRowMapper mapper = EntityMapperFactory.getMapper(entityClass);
        Field field = EntityUtils.idField(entityClass);
        Object id = ClassUtils.getValue(entity, field);
        Update update = new DefaultUpdate();
        update.target(entityClass);
        update.set(entity, ignoreNull);
        update.where(Wheres.equal(mapper.getIdName(), id));
        return updateBy(update);
    }


    /**
     * 添加一条数据
     *
     * @param insert
     * @return
     */
    final public Integer insertBy(final Insert insert) {
        String sql = insert.toSql();
        Object[] sqlValues = insert.getSqlValues();
        if (log.isDebugEnabled()) {
            log.debug(sql);
            log.debug(Arrays.toString(sqlValues));
        }
        return jdbcTemplate.update(sql, sqlValues);
    }

    /**
     * 根据条件删除数据
     *
     * @param delete
     * @return
     */
    final public Integer deleteBy(final Delete delete) {
        String sql = delete.toSql();
        Object[] sqlValues = delete.getSqlValues();
        if (log.isDebugEnabled()) {
            log.debug(sql);
            log.debug(Arrays.toString(sqlValues));
        }
        return jdbcTemplate.update(sql, sqlValues);
    }

    /**
     * 根据条件更新
     *
     * @param update
     * @return
     */
    final public Integer updateBy(final Update update) {
        String sql = update.toSql();
        Object[] sqlValues = update.getSqlValues();
        if (log.isDebugEnabled()) {
            log.debug(sql);
            log.debug(Arrays.toString(sqlValues));
        }
        return jdbcTemplate.update(sql, sqlValues);
    }

    /**
     * 根据条件查询
     * @param query 实现
     * @return
     */
    final public List selectBy(final Query query) {
        EntityTableRowMapper mapper = EntityMapperFactory.getMapper(query.getEntity());
        mapper.setJdbcPlus(this);
        String sql = query.toSql();
        Object[] sqlValues = query.getSqlValues();
        if (log.isDebugEnabled()) {
            log.debug(sql);
            log.debug(Arrays.toString(sqlValues));
        }
        return jdbcTemplate.query(sql, sqlValues, mapper);
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
