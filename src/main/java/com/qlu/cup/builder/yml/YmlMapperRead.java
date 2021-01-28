package com.qlu.cup.builder.yml;

import com.qlu.cup.io.Resources;
import com.qlu.cup.io.SacnYmlMapper;

import java.io.*;
import java.util.*;

/**
 * @program: cup
 * @description: 读取全部的映射文件
 * @author: liuwenhao
 * @create: 2021-01-27 21:59
 **/
public class YmlMapperRead {

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
}