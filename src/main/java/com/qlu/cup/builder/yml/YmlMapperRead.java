package com.qlu.cup.builder.yml;

import com.qlu.cup.io.SacnYmlMapper;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @program: cup
 * @description: 读取全部的映射文件
 * @author: liuwenhao
 * @create: 2021-01-27 21:59
 **/
public class YmlMapperRead {

    /**
     * @param mapperPath mapper.yml 文件路径
     * @return Map<Class < ?>, YNode>
     * @description: 将指定的mapper.yml文件读取成Map<Class < ?>, YNode>数据
     * @author liuwenaho
     * @date 2021/1/29 20:20
     */
    public static Map<Class<?>, YNode> getMapper(String mapperPath) {
        String path = mapperPath.substring(0, mapperPath.lastIndexOf("*"));
        String yml = mapperPath.substring(mapperPath.lastIndexOf("*"));
        List<File> ymlList = new ArrayList<>();
        Map<Class<?>, YNode> nodeMap = new HashMap<>(16);
        try {
            SacnYmlMapper.findFiles(path, yml, ymlList);
            for (File file : ymlList) {
                InputStream inputStream = new FileInputStream(file);
                nodeMap.putAll(new YmlMapperBuilder().builder(inputStream));
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return nodeMap;
    }

    /**
     * @param nameSpace 接口的class对象
     * @description: 检查一个类中是否有重载，如果有重载返回false，否则返回true
     * @author liuwenaho
     * @date 2021/1/29 20:26
     */
    public static boolean checkOverload(Class<?> nameSpace) {
        Method[] methList = nameSpace.getDeclaredMethods();
        Set<String> set = Arrays.stream(methList).map(method -> {
            String meth = method.toString().substring(0, method.toString().lastIndexOf("("));
            return meth.substring(meth.lastIndexOf(".") + 1);
        }).collect(Collectors.toSet());
        return set.size() != methList.length;
    }

}