package com.qlu.cup.mapper;

import com.qlu.cup.pojo.Person;
import com.qlu.cup.pojo.Users;
import com.qlu.cup.vo.FamilyVo;

import java.util.List;

/**
 * @author shidacaizi
 * @date 2020/4/13 21:30
 */
public interface PersonMapper {
    List<Person> getUserList();

    int addUser(Person person);

    Person getUserById(Integer id);

    int updateById(Person person);

    int deleteById(Integer id);

    List<FamilyVo> getFamily(Integer id);

    int creatTable(String tableName);
}
