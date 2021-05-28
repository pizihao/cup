package com.qlu.cup.pojo;

import java.io.Serializable;

/**
 * @author shidacaizi
 * @date 2020/4/13 21:41
 */
public class Person implements Serializable {

    private static final long serialVersionUID = -5329846354517798466L;
    private Integer id;
    private String name;
    private Integer age;

    public Person() {
    }

    public Person(Integer id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public Person(String ha, Integer i) {
        this(null, ha, i);
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
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
