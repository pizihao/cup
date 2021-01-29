package com.qlu.cup.mapper;

import com.qlu.cup.pojo.Person;
import com.qlu.cup.pojo.Users;

import java.util.List;

/**
 * @author shidacaizi
 * @date 2020/4/13 21:30
 */
public interface UsersMapper {
    List<Users> getUserList();

    int addUser(Users users);

    Users getUserById(Integer id);
}
