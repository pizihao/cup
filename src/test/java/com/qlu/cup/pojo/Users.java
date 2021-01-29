package com.qlu.cup.pojo;

import java.io.Serializable;

/**
 * @author shidacaizi
 * @date 2020/4/13 21:41
 */
public class Users implements Serializable {

    private static final long serialVersionUID = -5329846354517798466L;

    private int id;
    private String name;
    private int age;

    public Users() {
    }

    public Users(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public Users(String ha, int i) {
        this.name = name;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age='" + age + '\'' +
                '}';
    }
}
