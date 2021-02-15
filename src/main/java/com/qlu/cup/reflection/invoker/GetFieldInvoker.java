package com.qlu.cup.reflection.invoker;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * getter调用者
 */
public class GetFieldInvoker implements Invoker {
    private Field field;

    public GetFieldInvoker(Field field) {
        this.field = field;
    }

    //就是调用Field.get
    @Override
    public Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException {
        return field.get(target);
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }
}
