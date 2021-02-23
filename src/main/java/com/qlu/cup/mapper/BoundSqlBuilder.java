package com.qlu.cup.mapper;

import com.qlu.cup.bind.Configuration;
import com.qlu.cup.builder.StaticSqlSource;
import com.qlu.cup.builder.yml.YNode;
import com.qlu.cup.reflection.MetaClass;
import com.qlu.cup.result.ResultFlag;
import com.qlu.cup.result.ResultMap;
import com.qlu.cup.result.ResultMapping;
import com.qlu.cup.type.JdbcType;
import com.qlu.cup.type.TypeHandler;

import java.util.*;

/**
 * @program: cup
 * @description: 创建BoundSql
 * @author: liuwenhao
 * @create: 2021-01-28 22:24
 **/
public class BoundSqlBuilder {

    /**
     * @param nodeMap key为类全限定名，value为YNode对象的map集合
     * @return java.util.Map<java.lang.String, com.qlu.cup.mapper.BoundSql>
     * @description: 创建一个新的映射关系，将映射关系从接口-->映射文件细粒化为接口方法-->映射文件节点
     * @author liuwenaho
     * @date 2021/1/29 20:26
     */
    public static Map<String, BoundSql> builder(Map<String, YNode> nodeMap, Configuration configuration) {
        Map<String, BoundSql> hashMap = new HashMap<>(16);
        nodeMap.forEach((aClass, yNode) -> {
            String classId = yNode.getNamespace() + "." + yNode.getName();
            hashMap.put(classId, new BoundSql(yNode.getId(), yNode.getSql()
                    , yNode.getParameterType(), yNode.getResultType(), classId, configuration.getEnvironment()
                    , new StaticSqlSource(configuration, yNode.getSql())));
            List<ResultMapping> resultMappings = new ArrayList<>();
            resultMappings.add(buildResultMapping(ObjectInterface.getClazz(yNode.getResultType()), null, null, ObjectInterface.getClazz(classId),
                    null, null, null, null, null, null, new ArrayList<ResultFlag>(),
                    null, null, false, configuration));
            addResultMap(classId,ObjectInterface.getClazz(yNode.getResultType()),resultMappings,false,configuration);

        });
        return hashMap;
    }

    public static void addResultMap(String id, Class<?> type, List<ResultMapping> resultMappings, Boolean autoMapping, Configuration configuration) {
        //建造者模式
        ResultMap.Builder resultMapBuilder = new ResultMap.Builder(configuration, id, type, resultMappings, autoMapping);
        ResultMap resultMap = resultMapBuilder.build();
        configuration.addResultMap(resultMap);
    }

    public static ResultMapping buildResultMapping(
            Class<?> resultType,
            String property,
            String column,
            Class<?> javaType,
            JdbcType jdbcType,
            String nestedSelect,
            String nestedResultMap,
            String notNullColumn,
            String columnPrefix,
            Class<? extends TypeHandler<?>> typeHandler,
            List<ResultFlag> flags,
            String resultSet,
            String foreignColumn,
            boolean lazy,
            Configuration configuration) {
        Class<?> javaTypeClass = resolveResultJavaType(resultType, property, javaType);
        TypeHandler<?> typeHandlerInstance = resolveTypeHandler(javaTypeClass, typeHandler, configuration);
        //解析复合的列名,一般用不到，返回的是空
        List<ResultMapping> composites = parseCompositeColumnName(column, configuration);
        if (composites.size() > 0) {
            column = null;
        }
        //构建result map
        ResultMapping.Builder builder = new ResultMapping.Builder(configuration, property, column, javaTypeClass);
        builder.jdbcType(jdbcType);
        builder.nestedQueryId(nestedSelect);
        builder.nestedResultMapId(nestedResultMap);
        builder.resultSet(resultSet);
        builder.typeHandler(typeHandlerInstance);
        builder.flags(flags == null ? new ArrayList<ResultFlag>() : flags);
        builder.composites(composites);
        builder.notNullColumns(parseMultipleColumnNames(notNullColumn));
        builder.columnPrefix(columnPrefix);
        builder.foreignColumn(foreignColumn);
        builder.lazy(lazy);
        return builder.build();
    }

    private static Class<?> resolveResultJavaType(Class<?> resultType, String property, Class<?> javaType) {
        if (javaType == null && property != null) {
            try {
                MetaClass metaResultType = MetaClass.forClass(resultType);
                javaType = metaResultType.getSetterType(property);
            } catch (Exception e) {
                //ignore, following null check statement will deal with the situation
            }
        }
        if (javaType == null) {
            javaType = Object.class;
        }
        return javaType;
    }

    protected static TypeHandler<?> resolveTypeHandler(Class<?> javaType, Class<? extends TypeHandler<?>> typeHandlerType, Configuration configuration) {
        if (typeHandlerType == null) {
            return null;
        }
        //去typeHandlerRegistry查询对应的TypeHandler
        TypeHandler<?> handler = configuration.getTypeHandlerRegistry().getMappingTypeHandler(typeHandlerType);
        if (handler == null) {
            //如果没有在Registry找到，调用typeHandlerRegistry.getInstance来new一个TypeHandler返回
            handler = configuration.getTypeHandlerRegistry().getInstance(javaType, typeHandlerType);
        }
        return handler;
    }

    private static List<ResultMapping> parseCompositeColumnName(String columnName, Configuration configuration) {
        List<ResultMapping> composites = new ArrayList<ResultMapping>();
        if (columnName != null && (columnName.indexOf('=') > -1 || columnName.indexOf(',') > -1)) {
            StringTokenizer parser = new StringTokenizer(columnName, "{}=, ", false);
            while (parser.hasMoreTokens()) {
                String property = parser.nextToken();
                String column = parser.nextToken();
                ResultMapping.Builder complexBuilder = new ResultMapping.Builder(configuration, property, column, configuration.getTypeHandlerRegistry().getUnknownTypeHandler());
                composites.add(complexBuilder.build());
            }
        }
        return composites;
    }

    private static Set<String> parseMultipleColumnNames(String columnName) {
        Set<String> columns = new HashSet<String>();
        if (columnName != null) {
            if (columnName.indexOf(',') > -1) {
                StringTokenizer parser = new StringTokenizer(columnName, "{}, ", false);
                while (parser.hasMoreTokens()) {
                    String column = parser.nextToken();
                    columns.add(column);
                }
            } else {
                columns.add(columnName);
            }
        }
        return columns;
    }
}