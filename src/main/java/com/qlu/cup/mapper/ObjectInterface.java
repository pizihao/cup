package com.qlu.cup.mapper;

import java.util.*;

/**
 * @program: cup
 * @description: 接口及字符串解析成class
 * @author: liuwenhao
 * @create: 2021-01-29 11:44
 **/
public class ObjectInterface {

    public static Class<?> resolveInterface(Class<?> type) {
        Class<?> classToCreate;
        if (type == List.class || type == Collection.class || type == Iterable.class) {
            //List|Collection|Iterable-->ArrayList
            classToCreate = ArrayList.class;
        } else if (type == Map.class) {
            //Map->HashMap
            classToCreate = HashMap.class;
        } else if (type == SortedSet.class) {
            //SortedSet->TreeSet
            classToCreate = TreeSet.class;
        } else if (type == Set.class) {
            //Set->HashSet
            classToCreate = HashSet.class;
        } else {
            //除此以外，就用原来的类型
            classToCreate = type;
        }
        return classToCreate;
    }

    public static Class<?> getClazz(String str) {
        if (str == null || "".equals(str)) {
            return null;
        }
        Class<?> classToCreate = null;
        switch (str) {
            case "List":
            case "Collection":
            case "Iterable":
                classToCreate = ArrayList.class;
                break;
            case "Map":
                classToCreate = HashMap.class;
                break;
            case "SortedSet":
                classToCreate = TreeSet.class;
                break;
            case "Set":
                classToCreate = HashSet.class;
                break;
            case "int":
            case "Integer":
                classToCreate = int.class;
                break;
            case "double":
            case "Double":
                classToCreate = double.class;
                break;
            case "float":
            case "Float":
                classToCreate = float.class;
                break;
            case "long":
            case "Long":
                classToCreate = long.class;
                break;
            case "char":
            case "Character":
                classToCreate = char.class;
                break;
            case "short":
            case "Short":
                classToCreate = short.class;
                break;
            case "boolean":
            case "Boolean":
                classToCreate = boolean.class;
                break;
            case "void":
            case "Void":
                classToCreate = void.class;
                break;
            case "String":
                classToCreate = String.class;
                break;
            default:
                try {
                    classToCreate = Class.forName(str);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
        return classToCreate;
    }
}