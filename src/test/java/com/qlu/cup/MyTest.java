package com.qlu.cup;

import com.qlu.cup.cutil.CupUtil;
import com.qlu.cup.mapper.PersonMapper;
import com.qlu.cup.pojo.Person;
import com.qlu.cup.session.SqlSession;
import com.qlu.cup.vo.FamilyVo;
import org.junit.Test;

import java.util.List;

/**
 * @program: JVMDome
 * @description: 测试类
 * @author: liuwenhao
 * @create: 2021-01-18 18:55
 **/
public class MyTest {
    @Test
    public void insertTest() {
        PersonMapper mapper = getMapper(PersonMapper.class);
        int count = mapper.addUser(new Person("测试", 20));
        System.out.println(count);
    }

    @Test
    public void selectTest() {
        PersonMapper mapper = getMapper(PersonMapper.class);
        Person person = mapper.getUserById(500);
        System.out.println(person);
    }

    @Test
    public void mapperTest() {
        PersonMapper mapper = getMapper(PersonMapper.class);
        List<Person> userList = mapper.getUserList();
        userList.forEach(System.out::println);
    }

    @Test
    public void deleteTest() {
        PersonMapper mapper = getMapper(PersonMapper.class);
        int count = mapper.deleteById(500);
        System.out.println(count);
    }

    @Test
    public void updateTest() {
        PersonMapper mapper = getMapper(PersonMapper.class);
        int count = mapper.updateById(new Person(500,"修改测试",21));
        System.out.println(count);
    }

    @Test
    public void joinTest(){
        PersonMapper mapper = getMapper(PersonMapper.class);
        List<FamilyVo> family = mapper.getFamily(1);
        System.out.println(family);
    }

    public <T> T getMapper(Class<T> clazz){
        CupUtil cupUtil = new CupUtil();
        SqlSession sqlSession = cupUtil.getSqlSession();
        return sqlSession.getMapper(clazz);
    }

}