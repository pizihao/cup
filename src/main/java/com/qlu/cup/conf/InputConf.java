package com.qlu.cup.conf;

import com.qlu.cup.util.PartsUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @program: cup
 * @description: 读取配置文件，以流的形式读取
 * @author: liuwenhao
 * @create: 2021-01-23 17:14
 **/
public class InputConf {

    /**
     * @param inputStream 配置文件的流数据
     * @return java.util.Properties
     * @description: 接收一个流信息， 转换成Properties形式的字节流
     * @author liuwenaho
     * @date 2021/1/26 16:29
     */
    public static InputStream getInputStream(InputStream inputStream) {
        return new ByteArrayInputStream(getProperties(inputStream).toString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * @param inputStream 配置文件的流数据
     * @return java.io.Reader
     * @description: 接收一个流信息， 转换成Properties形式的字符流
     * @author liuwenaho
     * @date 2021/1/26 19:44
     */
    public static Reader getReader(InputStream inputStream) {
        return new StringReader(getProperties(inputStream).toString());
    }

    /**
     * @param reader 配置文件的流数据
     * @return java.io.InputStream
     * @description: 接收一个流信息， 转换成Properties形式的字节流
     * @author liuwenaho
     * @date 2021/1/26 21:35
     */
    public static InputStream getInputStream(Reader reader) {
        return new ByteArrayInputStream(getProperties(reader).toString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * @description: 接收一个流信息， 转换成Properties形式的字符流
     * @param reader 配置文件的流数据
     * @return java.io.Reader
     * @author liuwenaho
     * @date 2021/1/26 21:43
     */
    public static Reader getReader(Reader reader) {
        return new StringReader(getProperties(reader).toString());
    }

    /**
     * @param inputStream 配置文件的字节流
     * @return java.util.Properties
     * @description: 把一个字节流转换成Properties
     * @author liuwenaho
     * @date 2021/1/26 20:18
     */
    private static Properties getProperties(InputStream inputStream) {
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
    private static Properties getProperties(Reader reader) {
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
    private static Properties getProperties(String str) {
        Map<String, Object> map = YamlParser.yamlToMultilayerMap(str);
        if (!map.containsKey(PartsUtil.NAME) || map.containsKey(PartsUtil.DATA_SOURCE)) {
            throw new ConfException("缺少配置信息");
        }
        map = (Map<String, Object>) ((Map<String, Object>) map.get(PartsUtil.NAME)).get(PartsUtil.DATA_SOURCE);
        Properties properties = new Properties();
        if (map != null) {
            properties.putAll(map);
        }
        return properties;
    }

}