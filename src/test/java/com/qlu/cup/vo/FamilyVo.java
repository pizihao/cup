package com.qlu.cup.vo;

/**
 * @program: cup
 * @description: 一家人
 * @author: liuwenhao
 * @create: 2021-05-28 22:49
 **/
public class FamilyVo {
    private String personName;
    private String childName;

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    @Override
    public String toString() {
        return "FamilyVo{" +
                "personName='" + personName + '\'' +
                ", childName='" + childName + '\'' +
                '}';
    }
}