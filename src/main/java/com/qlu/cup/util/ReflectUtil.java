package com.qlu.cup.util;

import com.qlu.cup.exception.CupException;
import com.qlu.cup.mapper.ObjectInterface;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: cup
 * @description: 通过属性名获取set和get
 * @author: liuwenhao
 * @create: 2021-03-06 19:51
 **/
public class ReflectUtil {

    /**
     * 根据属性，获取get方法
     *
     * @param ob   对象
     * @param name 属性名
     */
    public static Object getGetMethod(Object ob, String name) throws Exception {
        Method[] m = ob.getClass().getMethods();
        for (int i = 0; i < m.length; i++) {
            if (("get" + name).toLowerCase().equals(m[i].getName().toLowerCase())) {
                return m[i].invoke(ob);
            }
        }
        return null;
    }

    /**
     * 根据属性，拿到set方法，并把值set到对象中
     *
     * @param obj   对象
     * @param clazz 对象的class
     */
    public static void setValue(Object obj, Class<?> clazz, String filedName, Class<?> typeClass, Object value) {
        filedName = removeLine(filedName);
        String methodName = "set" + filedName.substring(0, 1).toUpperCase() + filedName.substring(1);
        try {
            Method method = clazz.getDeclaredMethod(methodName, new Class[]{typeClass});
            method.invoke(obj, new Object[]{getClassTypeValue(typeClass, value)});
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 通过class类型获取获取对应类型的值
     *
     * @param typeClass class类型
     * @param value     值
     * @return Object
     */
    private static Object getClassTypeValue(Class<?> typeClass, Object value) {
        if (typeClass == int.class || value instanceof Integer) {
            if (null == value) {
                return 0;
            }
            return value;
        } else if (typeClass == short.class) {
            if (null == value) {
                return 0;
            }
            return value;
        } else if (typeClass == byte.class) {
            if (null == value) {
                return 0;
            }
            return value;
        } else if (typeClass == double.class) {
            if (null == value) {
                return 0;
            }
            return value;
        } else if (typeClass == long.class) {
            if (null == value) {
                return 0;
            }
            return value;
        } else if (typeClass == String.class) {
            if (null == value) {
                return "";
            }
            return value;
        } else if (typeClass == boolean.class) {
            if (null == value) {
                return true;
            }
            return value;
        } else if (typeClass == BigDecimal.class) {
            if (null == value) {
                return new BigDecimal(0);
            }
            return new BigDecimal(value + "");
        } else {
            return typeClass.cast(value);
        }
    }

    /**
     * 处理字符串  如：  abc_dex ---> abcDex
     *
     * @param str
     * @return
     */
    public static String removeLine(String str) {
        if (null != str && str.contains("_")) {
            int i = str.indexOf("_");
            char ch = str.charAt(i + 1);
            char newCh = (ch + "").substring(0, 1).toUpperCase().toCharArray()[0];
            String newStr = str.replace(str.charAt(i + 1), newCh);
            String newStr2 = newStr.replace("_", "");
            return newStr2;
        }
        return str;
    }

    public static String genericReturnType(String returnType) {
        if (returnType == null) {
            return null;
        }
        if (!returnType.contains("<")) {
            return returnType;
        }
        return returnType.substring(returnType.indexOf("<") + 1, returnType.lastIndexOf(">"));
    }

    public static String returnStringType(String ymlResult, String methodResult) {
        if (ymlResult == null && ("".equals(methodResult) || methodResult == null)) {
            throw new CupException("无法获取返回值类型，请检查配置文件或接口方法");
        }

        if (ymlResult != null && methodResult != null && !"".equals(methodResult)) {
            if (!ymlResult.equals(ObjectInterface.getClazz(methodResult).getName())) {
                throw new CupException("接口方法返回值与映射文件返回值无法对应，请检查映射文件");
            }
        }
        //优先使用yml映射文件的
        if (ymlResult != null) {
            return ymlResult;
        } else {
            return ObjectInterface.getClazz(methodResult).getName();
        }
    }

    /**
     * @description: 传入一个人map格式的字符串，修改为map
     * @author liuwenaho
     * @date 2021/3/7 22:44
     */
    public static Map<String, String> mapStringToMap(String str) {
        str = str.substring(1, str.length() - 1);
        String[] strs = str.split(",");
        Map<String, String> map = new HashMap<String, String>(16);
        for (String string : strs) {
            String key = string.split("=")[0].trim();
            String value = string.split("=")[1];
            map.put(key, value);
        }
        return map;
    }
}