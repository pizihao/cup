package com.qlu.cup.builder.yml;

import com.qlu.cup.bind.BindException;
import com.qlu.cup.bind.Configuration;
import com.qlu.cup.mapper.BoundSql;
import com.qlu.cup.mapper.MapperProxyFactory;
import com.qlu.cup.mapper.ObjectInterface;
import com.qlu.cup.parameter.ParameterMapping;
import com.qlu.cup.result.ResultMapping;
import com.qlu.cup.result.ResultType;
import com.qlu.cup.util.GenericTokenParser;
import com.qlu.cup.util.PartsUtil;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.*;
import java.util.*;

/**
 * @program: cup
 * @description: 通过读取映射文件构建一个mapper
 * @author: liuwenhao
 * @create: 2021-01-23 17:03
 **/
public class YmlMapperBuilder {
    private String rootName;
    private String namespace;

    /**
     * @param inputStream 映射文件字节流
     * @description: 接收一个映射文件流，生成映射文件信息
     * @author liuwenaho
     * @date 2021/1/27 13:45
     */
    public Map<String, BoundSql> builder(InputStream inputStream, Configuration configuration) throws ClassNotFoundException {
        return builder(InputMapper.getProperties(inputStream), configuration);
    }

    /**
     * @param reader 映射文件字符流
     * @description: 接收一个映射文件流，生成映射文件信息
     * @author liuwenaho
     * @date 2021/1/27 13:45
     */
    public Map<String, BoundSql> builder(Reader reader, Configuration configuration) throws ClassNotFoundException {
        return builder(InputMapper.getProperties(reader), configuration);
    }

    /**
     * @param map 流转成的map
     * @description: TODO
     * @author liuwenaho
     * @date 2021/1/27 22:09
     */
    public Map<String, BoundSql> builder(Map<String, Object> map, Configuration configuration) throws ClassNotFoundException {
        //根节点
        if (!map.containsKey(PartsUtil.MAPPER)) {
            throw new MapperException("缺少映射信息");
        }
        rootName = PartsUtil.MAPPER;
        map = getMap(map, rootName);
        //命名空间
        if (!map.containsKey(PartsUtil.NAME_SPACE)) {
            throw new MapperException("未注明命名空间");
        }
        namespace = (String) map.get(PartsUtil.NAME_SPACE);
        map.remove(PartsUtil.NAME_SPACE);
        //子节点
        Map<String, BoundSql> sqlMap = new HashMap<>(16);
        configuration.setKnownMappers(Class.forName(namespace), new MapperProxyFactory<>(Class.forName(namespace)));
        map.forEach((handle, value) -> {
            try {
                sqlMap.putAll(getYNode(handle, (Map<String, Object>) value, configuration));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        return sqlMap;
    }

    private Map<String, Object> getMap(Map<String, Object> map, String str) {
        return (Map<String, Object>) map.get(str);
    }

    /**
     * @param obj yml文件中的最底层节点
     * @description: TODO
     * @author liuwenaho
     * @date 2021/1/27 21:33
     */
    private Map<String, BoundSql> getYNode(String handle, Map<String, Object> obj, Configuration configuration) throws ClassNotFoundException {
        Map<String, BoundSql> sqlMap = new HashMap<>(16);
        for (Map.Entry<String, Object> entry : obj.entrySet()) {
            //存在节点信息，一个节点对应一个方法
            Map<String, Object> objectMap = (Map<String, Object>) entry.getValue();
            BoundSql boundSql = BoundSql.builder().environment(configuration.getEnvironment()).namespace(namespace).
                    handle(handle).name(entry.getKey()).nameId(namespace + "." + entry.getKey()).build();
            //参数映射和结果映射
            Class<?> aClass = Class.forName(namespace);
            if (YmlMapperRead.checkOverload(aClass)) {
                throw new BindException("禁止在" + namespace + "中出现方法重载");
            }
            if (objectMap.containsKey(PartsUtil.PARAMETER_TYPE)) {
                boundSql.setParameter(objectMap.get(PartsUtil.PARAMETER_TYPE).toString());
            }
            if (objectMap.containsKey(PartsUtil.SQL)) {
                boundSql.setParameterIndex(getParameterIndex(objectMap.get(PartsUtil.SQL).toString(), boundSql));
            } else {
                throw new MapperException("请检查映射文件的正确性");
            }
            //只拿取public方法，出于是接口的考虑
            Method[] aClassMethods = aClass.getMethods();
            for (Method aClassMethod : aClassMethods) {
                Map<String, ParameterMapping> parameterMap = new HashMap<>(16);
                Map<String, ResultMapping> resultMap = new HashMap<>(16);
                if (aClassMethod.getName().equals(entry.getKey())) {
                    //获取方法的参数
                    Parameter[] parameters = aClassMethod.getParameters();
                    for (Parameter parameter : parameters) {
                        //判断参数类型的类加载器
                        if (parameter.getType().getClassLoader() == null) {
                            parameterMap.put(parameter.getName(), ParameterMapping.builder().configuration(configuration).
                                    namespace(namespace).name(aClassMethod.getName()).parameterName(parameter.getName()).
                                    javaType(parameter.getType()).build());
                        } else {
                            Field[] fields = getAllFields(parameter.getType());
                            for (Field field : fields) {
                                parameterMap.put(field.getName(), ParameterMapping.builder().configuration(configuration).
                                        namespace(namespace).name(aClassMethod.getName()).parameterName(field.getName()).
                                        javaType(field.getType()).build());
                            }
                        }
                    }
                    boundSql.setParameterMap(parameterMap);
                    //获取结果映射
                    //获取结果，也就是返回类型
                    //接口方法返回值包括元素类型
                    boundSql.setResult(aClassMethod.getGenericReturnType().getTypeName());
                    String resultYmlType = null;
                    if (objectMap.containsKey(PartsUtil.RESULT_TYPE)) {
                        //yml映射文件上写的resultType
                        resultYmlType = ObjectInterface.getClazz(objectMap.get(PartsUtil.RESULT_TYPE).toString()).getTypeName();
                        Class<?> ymlResultClass = ObjectInterface.getClazz(objectMap.get(PartsUtil.RESULT_TYPE).toString());
                        ClassLoader classLoader = ymlResultClass.getClassLoader();
                        //加载是null就进行单项映射
                        if (classLoader == null) {
                            resultMap.put(namespace + "." + entry.getKey(), ResultMapping.builder()
                                    .name(ymlResultClass.getName()).type(ymlResultClass)
                                    .className(ymlResultClass.getName()).build());
                        } else {
                            Field[] fields = getAllFields(ymlResultClass);
                            for (Field field : fields) {
                                resultMap.put(field.getName(), ResultMapping.builder()
                                        .name(field.getName()).type(field.getType())
                                        .className(ymlResultClass.getName()).build());
                            }
                        }
                    } else {
                        //没有的话去看方法的返回值
                        Type genericReturnType = aClassMethod.getGenericReturnType();
                        if (genericReturnType instanceof ParameterizedType) {
                            //通过返回值去获取泛型类
                            Type[] actualTypeArguments = ((ParameterizedType) genericReturnType).getActualTypeArguments();
                            for (Type actualArgument : actualTypeArguments) {
                                //actualArgument就是那个类型
                                Field[] fields = getAllFields(Class.forName(actualArgument.getTypeName()));
                                for (Field field : fields) {
                                    resultMap.put(field.getName(), ResultMapping.builder()
                                            .name(field.getName()).type(field.getType())
                                            .className(actualArgument.getTypeName()).build());
                                }
                            }
                        } else if (ObjectInterface.getClazz(genericReturnType.getTypeName()).getClassLoader() != null) {
                            Field[] fields = getAllFields(Class.forName(ObjectInterface.getClazz(genericReturnType.getTypeName()).getTypeName()));
                            for (Field field : fields) {
                                resultMap.put(field.getName(), ResultMapping.builder()
                                        .name(field.getName()).type(field.getType())
                                        .className(ObjectInterface.getClazz(genericReturnType.getTypeName()).getTypeName()).build());
                            }
                        } else {
                            resultMap.put(namespace + "." + entry.getKey(), ResultMapping.builder()
                                    .name(ObjectInterface.getClazz(genericReturnType.getTypeName()).getName()).type(ObjectInterface.getClazz(genericReturnType.getTypeName()))
                                    .className(ObjectInterface.getClazz(genericReturnType.getTypeName()).getName()).build());
                        }
                    }
                    ResultType resultType = ResultType.builder().name(aClassMethod.getName()).namespace(namespace).configuration(configuration)
                            .resultType(resultYmlType).resultMap(resultMap).build();
                    configuration.setResultMap(namespace + "." + entry.getKey(), resultType);
                    //是否需要返回自增id
                    if (objectMap.containsKey(PartsUtil.GENERATED_KEY)) {
                        boundSql.setGeneratedKey(Boolean.parseBoolean(objectMap.get(PartsUtil.GENERATED_KEY).toString()));
                    } else {
                        boundSql.setGeneratedKey(Boolean.parseBoolean(PartsUtil.GENERATED_KEY_NAME));
                    }
                    //SQL类型
                    if (objectMap.containsKey(PartsUtil.SQL_TYPE)) {
                        boundSql.setSqlType(objectMap.get(PartsUtil.SQL_TYPE).toString());
                    } else {
                        boundSql.setSqlType(PartsUtil.SQL_TYPE_NAME);
                    }
                    //生成最终的BoundSql
                    boundSql.setParameterMap(parameterMap);
                    boundSql.setResultType(resultType);
                    boundSql.setConfiguration(configuration);
                    break;
                }
            }
            sqlMap.put(namespace + "." + entry.getKey(), boundSql);
        }
        return sqlMap;
    }

    public static Field[] getAllFields(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        return fieldList.toArray(fields);
    }

    public static Map<String, Integer> getParameterIndex(String sql, BoundSql boundSql) {
        //获取 ${ 和 } 中间的信息
        GenericTokenParser parser = new GenericTokenParser("${", "}");
        return parser.parse(sql, boundSql);
    }
}