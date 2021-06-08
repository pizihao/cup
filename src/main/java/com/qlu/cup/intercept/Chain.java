package com.qlu.cup.intercept;


import java.util.List;

/**
 * @program: JVMDome
 * @description: 拦截器链
 * @author: liuwenhao
 * @create: 2021-06-06 11:38
 **/
public class Chain {

    private List<Interceptable> interceptorTests;

    public void addInterceptorTests(Interceptable interceptable) {
        interceptorTests.add(interceptable);
    }

    public Object invokeInterceptor(Object target) {
        //循环调用拦截器中的方法，在初始化的时候改变对象的行为，如处理器和执行器
        interceptorTests.forEach(interpretable -> interpretable.plugIn(target));
        return target;
    }

}