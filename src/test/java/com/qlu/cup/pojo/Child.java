package com.qlu.cup.pojo;

/**
 * @program: cup
 * @description: 孩子类
 * @author: liuwenhao
 * @create: 2021-05-28 22:47
 **/
public class Child {
    private Integer id;
    private String name;
    private Integer pId;

    public Child() {
    }

    public Child(Integer id, String name, Integer pId) {
        this.id = id;
        this.name = name;
        this.pId = pId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getpId() {
        return pId;
    }

    public void setpId(Integer pId) {
        this.pId = pId;
    }
}