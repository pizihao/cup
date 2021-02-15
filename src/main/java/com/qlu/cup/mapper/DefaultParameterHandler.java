package com.qlu.cup.mapper;

import com.qlu.cup.bind.Configuration;
import com.qlu.cup.context.ErrorContext;
import com.qlu.cup.statement.ParameterMapping;
import com.qlu.cup.type.JdbcType;
import com.qlu.cup.type.TypeHandler;
import com.qlu.cup.type.TypeHandlerRegistry;
import com.qlu.cup.reflection.MetaObject;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * 默认参数处理器
 */
public class DefaultParameterHandler implements ParameterHandler {

    private final TypeHandlerRegistry typeHandlerRegistry;

    private final Object parameterObject;
    private BoundSql boundSql;
    private Configuration configuration;

    public DefaultParameterHandler(Object parameterObject, BoundSql boundSql) {
        this.configuration = Configuration.getConfiguration(boundSql.getEnvironment());
        this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        this.parameterObject = parameterObject;
        this.boundSql = boundSql;
    }

    @Override
    public Object getParameterObject() {
        return parameterObject;
    }

    //设置参数
    @Override
    public void setParameters(PreparedStatement ps) throws SQLException {
        ErrorContext.instance().activity("setting parameters").object(boundSql.getParameterMap().getId());
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null) {
            //循环设参数
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                Object value;
                String propertyName = parameterMapping.getProperty();
                if (parameterObject == null) {
                    //若参数为null，直接设null
                    value = null;
                } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                    //若参数有相应的TypeHandler，直接设object
                    value = parameterObject;
                } else {
                    //除此以外，MetaObject.getValue反射取得值设进去
                    MetaObject metaObject = configuration.newMetaObject(parameterObject);
                    value = metaObject.getValue(propertyName);
                }
                TypeHandler typeHandler = parameterMapping.getTypeHandler();
                JdbcType jdbcType = parameterMapping.getJdbcType();
                if (value == null && jdbcType == null) {
                    //不同类型的set方法不同，所以委派给子类的setParameter方法
                    jdbcType = configuration.getJdbcTypeForNull();
                }
                typeHandler.setParameter(ps, i + 1, value, jdbcType);
            }
        }
    }

}
