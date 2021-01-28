package com.qlu.cup.builder.yml;

import com.qlu.cup.util.PartsUtil;

import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: cup
 * @description: 通过读取映射文件构建一个mapper
 * @author: liuwenhao
 * @create: 2021-01-23 17:03
 **/
public class YmlMapperBuilder {
    private String rootName;
    private String namespace;
    private Map<String, YNode> nodeMap = new HashMap<>(16);

    /**
     * @param inputStream 映射文件字节流
     * @description: 接收一个映射文件流，生成映射文件信息
     * @author liuwenaho
     * @date 2021/1/27 13:45
     */
    public Map<Class<?>, YNode> builder(InputStream inputStream) throws ClassNotFoundException {
        return builder(InputMapper.getProperties(inputStream));
    }

    /**
     * @param reader 映射文件字符流
     * @description: 接收一个映射文件流，生成映射文件信息
     * @author liuwenaho
     * @date 2021/1/27 13:45
     */
    public Map<Class<?>, YNode> builder(Reader reader) throws ClassNotFoundException {
        return builder(InputMapper.getProperties(reader));
    }

    /**
     * @param map 流转成的map
     * @return java.util.Map<java.lang.Class < ?>,com.qlu.cup.builder.yml.YNode>
     * @description: TODO
     * @author liuwenaho
     * @date 2021/1/27 22:09
     */
    public Map<Class<?>, YNode> builder(Map<String, Object> map) throws ClassNotFoundException {
        //根节点
        if (!map.containsKey(PartsUtil.MAPPER)) {
            throw new MapperException("缺少映射信息");
        }
        rootName = PartsUtil.MAPPER;
        map = getMap(map, rootName);
        //命名空间
        if (!map.containsKey(PartsUtil.NAME_SPACE)) {
            throw new MapperException("未注明命名空间");
        }
        namespace = (String) map.get(PartsUtil.NAME_SPACE);
        map.remove(PartsUtil.NAME_SPACE);
        //子节点

        map.forEach((handle, value) -> {
            getYNode(handle, (Map<String, Object>) value);
        });
        YNode yNode = new YNode.Builder(rootName, nodeMap, null, namespace, rootName).build();
        Map<Class<?>, YNode> classYNodeMap = new HashMap<Class<?>, YNode>(1);
        classYNodeMap.put(Class.forName(namespace), yNode);
        return classYNodeMap;
    }

    private Map<String, Object> getMap(Map<String, Object> map, String str) {
        return (Map<String, Object>) map.get(str);
    }

    /**
     * @param obj yml文件中的最底层节点
     * @return void
     * @description: TODO
     * @author liuwenaho
     * @date 2021/1/27 21:33
     */
    private void getYNode(String handle, Map<String, Object> obj) {
        for (Map.Entry<String, Object> entry : obj.entrySet()) {
            Map<String, Object> objectMap = (Map<String, Object>) entry.getValue();
            YNode.Builder node = new YNode.Builder(entry.getKey(), null, entry.getValue().toString(), namespace, rootName);
            node.setId(handle);
            if (objectMap.containsKey(PartsUtil.PARAMETER_TYPE)) {
                node.setParameterType(objectMap.get(PartsUtil.PARAMETER_TYPE).toString());
            }
            if (objectMap.containsKey(PartsUtil.RESULT_TYPE)) {
                node.setResultType(objectMap.get(PartsUtil.RESULT_TYPE).toString());
            }
            if (objectMap.containsKey(PartsUtil.SQL)) {
                node.setSql(objectMap.get(PartsUtil.SQL).toString());
            }
            nodeMap.put(namespace + "." + entry.getKey(), node.build());
        }
    }
}