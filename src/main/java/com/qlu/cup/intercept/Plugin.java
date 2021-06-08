package com.qlu.cup.intercept;

import com.qlu.cup.annotation.Intercept;
import com.qlu.cup.annotation.Signature;
import com.qlu.cup.exception.CupException;
import com.qlu.cup.util.ExceptionUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 插件
 *
 * @program: cup
 * @author: liuwenhao
 * @create: 2021-03-03 13:16
 **/
public class Plugin implements InvocationHandler {

    private Object target;
    private Interceptable interceptor;
    private Map<Class<?>, Set<Method>> signatureMap;

    private Plugin(Object target, Interceptable interceptor, Map<Class<?>, Set<Method>> signatureMap) {
        this.target = target;
        this.interceptor = interceptor;
        this.signatureMap = signatureMap;
    }

    public static Object wrap(Object target, Interceptable interceptor) {
        //取得签名Map
        Map<Class<?>, Set<Method>> signatureMap = getSignatureMap(interceptor);
        //取得要改变行为的类
        Class<?> type = target.getClass();
        //取得接口
        Class<?>[] interfaces = getAllInterfaces(type, signatureMap);
        //产生代理
        if (interfaces.length > 0) {
            return Proxy.newProxyInstance(
                    type.getClassLoader(),
                    interfaces,
                    new Plugin(target, interceptor, signatureMap));
        }
        return target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            Set<Method> methods = signatureMap.get(method.getDeclaringClass());
            //看哪些方法需要拦截
            if (methods != null && methods.contains(method)) {
                //调用Interceptor.interceptor，也即插入了我们自己的逻辑
                return interceptor.interceptor(new Variable(target, method, args));
            }
            //最后还是执行原来逻辑
            return method.invoke(target, args);
        } catch (Exception e) {
            throw ExceptionUtil.unwrapThrowable(e);
        }
    }

    private static Map<Class<?>, Set<Method>> getSignatureMap(Interceptable interceptor) {
        Intercept intercept = interceptor.getClass().getAnnotation(Intercept.class);

        if (intercept == null) {
            throw new CupException("拦截器类必须使用 @Intercept 标注" + interceptor.getClass().getName());
        }
        Signature[] signs = intercept.value();
        Map<Class<?>, Set<Method>> signatureMap = new HashMap<Class<?>, Set<Method>>();
        for (Signature sig : signs) {
            Set<Method> methods = signatureMap.computeIfAbsent(sig.type(), k -> new HashSet<>());
            try {
                Method method = sig.type().getMethod(sig.method(), sig.args());
                methods.add(method);
            } catch (NoSuchMethodException e) {
                throw new CupException("找不到的方法" + e, e);
            }
        }
        return signatureMap;
    }

    private static Class<?>[] getAllInterfaces(Class<?> type, Map<Class<?>, Set<Method>> signatureMap) {
        Set<Class<?>> interfaces = new HashSet<Class<?>>();
        while (type != null) {
            for (Class<?> c : type.getInterfaces()) {
                if (signatureMap.containsKey(c)) {
                    interfaces.add(c);
                }
            }
            type = type.getSuperclass();
        }
        return interfaces.toArray(new Class<?>[0]);
    }

}
