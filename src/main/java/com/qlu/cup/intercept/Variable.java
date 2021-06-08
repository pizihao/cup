package com.qlu.cup.intercept;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 拦截器要改变的数据，包括对象以及属性，调用method.invoke
 *
 * @program: JVMDome
 * @description:
 * @author: liuwenhao
 * @create: 2021-06-04 17:27
 **/
public class Variable {

    /**
     * 对象
     */
    private Object variableObject;

    /**
     * 方法
     */
    private Method variableMethod;

    /**
     * 属性参数
     */
    private Object[] variableParameter;

    public Variable(Object variableObject, Method variableMethod, Object[] variableParameter) {
        this.variableObject = variableObject;
        this.variableMethod = variableMethod;
        this.variableParameter = variableParameter;
    }

    public Object handle() throws InvocationTargetException, IllegalAccessException {
        return variableMethod.invoke(variableObject, variableParameter);
    }

}