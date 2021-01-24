package com.qlu.cup.makers.maker;

import com.qlu.cup.makers.where.Where;
import com.qlu.cup.builder.annotations.EntityTableRowMapper;
import java.util.List;

/**
 * @program: cup
 * @description: sql生成
 * @author: liuwenhao
 * @create: 2020-12-21 16:13
 **/
public interface SqlMaker {

    /**
     * 设置目标
     * entity 的 Class 必须添加@Table注解
     *
     * @param entity
     * @return
     */
    SqlMaker target(final Class entity);

    /**
     * sql是否构建完成
     */
    boolean isSqlComplete();

    /**
     * sqlValue是否构建完成
     */
    boolean isSqlValueComplete();

    /**
     * 获取数据库表对应的entity.class
     * @return
     */
    Class<?> getEntity();

    /**
     * 获取表的名称
     * @return
     */
    String getTableName();

    /**
     * 获取sql
     * @return
     */
    String toSql();

    /**
     * 获取sql 中的数据
     * @return
     */
    Object[] getSqlValues();

    /**
     * 获取 Entity与Table 的映射
     * @return
     */
    EntityTableRowMapper getEntityTableRowMapper();

    /**
     * 添加条件(可变参数类型)
     * @param wheres
     * @return
     */
    SqlMaker where(Where... wheres);

    /**
     * 添加条件(List)
     * @param wheres
     * @return
     */
    SqlMaker where(List<Where> wheres);

}
