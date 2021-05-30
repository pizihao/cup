package com.qlu.cup.mapper;

import com.qlu.cup.bind.BindException;
import com.qlu.cup.bind.Configuration;
import com.qlu.cup.builder.yml.MapperException;
import com.qlu.cup.builder.yml.YmlMapperBuilder;
import com.qlu.cup.util.ReflectUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * @program: cup
 * @description: 方法签名
 * @author: liuwenhao
 * @create: 2021-01-28 17:55
 **/
public class MethodSignature {

    private final boolean returnsMany;
    private final boolean returnsMap;
    private final boolean returnsVoid;
    private final Class<?> returnType;
    private final SortedMap<Integer, String> params;

    public MethodSignature(Method method) {
        this.returnType = method.getReturnType();
        this.returnsVoid = void.class.equals(this.returnType);
        this.returnsMany = (Collection.class.isAssignableFrom(this.returnType) || this.returnType.isArray());
        this.returnsMap = false;
        this.params = Collections.unmodifiableSortedMap(getParams(method));
    }

    public Object convertArgsToSqlCommandParam(Configuration configuration, String statement, Object[] args) throws IllegalAccessException {
        Map<String, Integer> parameterIndex = configuration.getMappedYnode(statement).getParameterIndex();
        final int paramCount = params.size();
//        if (paramCount != parameterIndex.size()) {
//            throw new MapperException("参数映射无法匹配");
//        }
        if (args == null || paramCount == 0) {
            return null;
        } else {
            Map<String, Object> argsMap = new HashMap<>(16);
            for (Object arg : args) {
                Class<?> aClass = arg.getClass();
                if (aClass.getClassLoader() != null) {
                    Field[] allFields = YmlMapperBuilder.getAllFields(aClass);
                    for (Field allField : allFields) {
                        allField.setAccessible(true);
                        //判断属性是否是静态的,并且在parameterIndex中是存在的
                        if (!Modifier.isStatic(allField.getModifiers()) && parameterIndex.get(allField.getName()) != null) {
                            argsMap.put(allField.getName(), allField.get(arg));
                        }
                    }
                } else {
                    //一个参数和多个参数是不同的
                    if (paramCount == 1 && parameterIndex.size() == 1) {
                        parameterIndex.forEach((s, integer) -> argsMap.put(s, args[0]));
                    } else {
                        argsMap.putAll(ReflectUtil.strStringToMap(params,Arrays.toString(args)));
                    }
                }
            }
            final Map<String, Object> param = new HashMap<>(16);
            //循环，参数中有的才放入，没有的使用初始值
            for (Map.Entry<Integer, String> entry : params.entrySet()) {
                if (argsMap.get(entry.getValue()) != null) {
                    param.put(entry.getValue(), argsMap.get(entry.getValue()));
                }
            }
            return param;
        }
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public boolean returnsMany() {
        return returnsMany;
    }

    public boolean returnsMap() {
        return returnsMap;
    }

    public boolean returnsVoid() {
        return returnsVoid;
    }

    private Integer getUniqueParamIndex(Method method, Class<?> paramType) {
        Integer index = null;
        final Class<?>[] argTypes = method.getParameterTypes();
        for (int i = 0; i < argTypes.length; i++) {
            if (paramType.isAssignableFrom(argTypes[i])) {
                if (index == null) {
                    index = i;
                } else {
                    throw new BindException(method.getName() + " cannot have multiple " + paramType.getSimpleName() + " parameters");
                }
            }
        }
        return index;
    }

    //得到所有参数
    private SortedMap<Integer, String> getParams(Method method) {
        Parameter[] parameters = method.getParameters();
        //用一个TreeMap,这样就保证还是按参数的先后顺序
        final SortedMap<Integer, String> params = new TreeMap<Integer, String>();
        final Class<?>[] argTypes = method.getParameterTypes();
        int count = 0;
        for (int i = 0; i < argTypes.length; i++) {
            if (argTypes[i].getClassLoader() == null) {
                params.put(++count, parameters[i].getName());
            } else {
                Field[] fields = YmlMapperBuilder.getAllFields(argTypes[i]);
                for (Field field : fields) {
                    params.put(++count, field.getName());
                }
            }
        }
        return params;
    }

}