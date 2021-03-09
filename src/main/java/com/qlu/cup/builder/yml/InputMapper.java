package com.qlu.cup.builder.yml;

import com.qlu.cup.builder.YamlParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program: cup
 * @description: 映射文件转化类
 * @author: liuwenhao
 * @create: 2021-01-27 19:37
 **/
public class InputMapper {

    /**
     * @param inputStream 配置文件的字节流
     * @return java.util.Properties
     * @description: 把一个字节流转换成Properties
     * @author liuwenaho
     * @date 2021/1/26 20:18
     */
    public static Map<String, Object> getProperties(InputStream inputStream) {
        return getProperties(new BufferedReader(new InputStreamReader(inputStream))
                .lines().parallel().collect(Collectors.joining(System.lineSeparator())));
    }

    /**
     * @param reader 配置文件的字节流
     * @return java.util.Properties
     * @description: 把一个字节流转换成Properties
     * @author liuwenaho
     * @date 2021/1/26 20:18
     */
    public static Map<String, Object> getProperties(Reader reader) {
        return getProperties(new BufferedReader(reader).lines().parallel()
                .collect(Collectors.joining(System.lineSeparator())));
    }

    /**
     * @param str 配置信息
     * @return java.util.Properties
     * @description: 中转
     * @author liuwenaho
     * @date 2021/1/26 21:41
     */
    private static Map<String, Object> getProperties(String str) {
        return YamlParser.yamlToMultilayerMap(str);
    }
}