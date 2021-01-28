package com.qlu.cup.mapper;

import com.qlu.cup.bind.BindException;
import com.qlu.cup.bind.Configuration;
import com.qlu.cup.result.ResultProcessor;
import com.qlu.cup.session.RowBounds;

import java.lang.reflect.Method;
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
    private final Integer resultHandlerIndex;
    private final Integer rowBoundsIndex;
    private final SortedMap<Integer, String> params;

    public MethodSignature(Configuration configuration, Method method) {
        this.returnType = method.getReturnType();
        this.returnsVoid = void.class.equals(this.returnType);
        this.returnsMany = (Collection.class.isAssignableFrom(this.returnType) || this.returnType.isArray());
        this.returnsMap = false;
        //记下RowBounds是第几个参数
        this.rowBoundsIndex = getUniqueParamIndex(method, RowBounds.class);
        //记下ResultHandler是第几个参数
        this.resultHandlerIndex = getUniqueParamIndex(method, ResultProcessor.class);
        this.params = Collections.unmodifiableSortedMap(getParams(method, false));
    }

    public Object convertArgsToSqlCommandParam(Object[] args) {
        final int paramCount = params.size();
        if (args == null || paramCount == 0) {
            //如果没参数
            return null;
        } else if (paramCount == 1) {
            //如果只有一个参数
            return args[params.keySet().iterator().next().intValue()];
        } else {
            //否则，返回一个ParamMap，修改参数名，参数名就是其位置
            final Map<String, Object> param = new ParamMap<Object>();
            int i = 0;
            for (Map.Entry<Integer, String> entry : params.entrySet()) {
                //1.先加一个#{0},#{1},#{2}...参数
                param.put(entry.getValue(), args[entry.getKey().intValue()]);
                // issue #71, add param names as param1, param2...but ensure backward compatibility
                final String genericParamName = "param" + String.valueOf(i + 1);
                if (!param.containsKey(genericParamName)) {
                    //2.再加一个#{param1},#{param2}...参数
                    //你可以传递多个参数给一个映射器方法。
                    //默认情况下它们将会以它们在参数列表中的位置来命名,比如:#{param1},#{param2}等。
                    //如果你想改变参数的名称(只在多参数情况下) ,那么你可以在参数上使用@Param(“paramName”)注解。
                    param.put(genericParamName, args[entry.getKey()]);
                }
                i++;
            }
            return param;
        }
    }

    public boolean hasRowBounds() {
        return rowBoundsIndex != null;
    }

    public RowBounds extractRowBounds(Object[] args) {
        return hasRowBounds() ? (RowBounds) args[rowBoundsIndex] : null;
    }

    public boolean hasResultHandler() {
        return resultHandlerIndex != null;
    }

    public ResultProcessor extractResultHandler(Object[] args) {
        return hasResultHandler() ? (ResultProcessor) args[resultHandlerIndex] : null;
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
    private SortedMap<Integer, String> getParams(Method method, boolean hasNamedParameters) {
        //用一个TreeMap,这样就保证还是按参数的先后顺序
        final SortedMap<Integer, String> params = new TreeMap<Integer, String>();
        final Class<?>[] argTypes = method.getParameterTypes();
        for (int i = 0; i < argTypes.length; i++) {
            //是否不是RowBounds/ResultHandler类型的参数
            if (!RowBounds.class.isAssignableFrom(argTypes[i]) && !ResultProcessor.class.isAssignableFrom(argTypes[i])) {
                //参数名字默认为0,1,2
                String paramName = String.valueOf(params.size());
                params.put(i, paramName);
            }
        }
        return params;
    }

}