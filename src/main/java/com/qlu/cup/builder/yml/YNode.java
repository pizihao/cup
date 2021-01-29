package com.qlu.cup.builder.yml;

import java.util.List;
import java.util.Map;

/**
 * @program: cup
 * @description: 节点，存储yml中的信息,一个映射文件对应一个Ynode
 * @author: liuwenhao
 * @create: 2021-01-27 11:26
 **/
//@Data
//@Builder(toBuilder = true)
public class YNode {

    /**
     * 节点名称
     */
    private String name;

    /**
     * 子节点，没有子节点的话为空， String 为 接口方法名
     */
    private Map<String,YNode> node;

    /**
     * 节点内容
     */
    private String value;

    /**
     * 节点所属命名空间，也有可能是命名节点本身
     * 根节点的namespace为null，可以用这个来判断是否是根节点
     */
    private String namespace;

    /**
     * 节点所属根节点名称,一般都是mapper
     */
    private String rootName;

    /**
     * id,非必须节点，叶子节点特有,代表操作，如select insert delete update
     */
    private String id;

    /**
     * sql,非必须节点，叶子节点特有
     */
    private String sql;

    /**
     * resultType,非必须节点，叶子节点特有
     */
    private String resultType;

    /**
     * parameterType,非必须节点，叶子节点特有
     */
    private String parameterType;

    private YNode(Builder builder) {
        this.name = builder.name;
        this.node = builder.node;
        this.value = builder.value;
        this.namespace = builder.namespace;
        this.rootName = builder.rootName;
        this.id = builder.id;
        this.sql = builder.sql;
        this.resultType = builder.resultType;
        this.parameterType = builder.parameterType;
    }

    public String getName() {
        return name;
    }

    public Map<String,YNode> getNode() {
        return node;
    }

    public String getValue() {
        return value;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getRootName() {
        return rootName;
    }

    public String getId() {
        return id;
    }

    public String getSql() {
        return sql;
    }

    public String getResultType() {
        return resultType;
    }

    public String getParameterType() {
        return parameterType;
    }

    static class Builder {
        private String name;
        private Map<String,YNode> node;
        private String value;
        private String namespace;
        private String rootName;
        private String id;
        private String sql;
        private String resultType;
        private String parameterType;
        public Builder() {
        }
        public Builder(String name, Map<String,YNode> node, String value, String namespace, String rootName) {
            this.name = name;
            this.node = node;
            this.value = value;
            this.namespace = namespace;
            this.rootName = rootName;
        }
        public YNode build() {
            return new YNode(this);
        }
        public Builder setId(String id) {
            this.id = id;
            return this;
        }
        public Builder setSql(String sql) {
            this.sql = sql;
            return this;
        }
        public Builder setResultType(String resultType) {
            this.resultType = resultType;
            return this;
        }
        public Builder setParameterType(String parameterType) {
            this.parameterType = parameterType;
            return this;
        }
    }
}