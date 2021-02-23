package com.qlu.cup;

import com.qlu.cup.bind.Configuration;
import com.qlu.cup.builder.yml.YmlMapperRead;
import com.qlu.cup.cutil.CupUtil;
import com.qlu.cup.cutil.DbUtil;
import com.qlu.cup.mapper.UsersMapper;
import com.qlu.cup.pojo.Users;
import com.qlu.cup.session.DefSqlSessionFactory;
import com.qlu.cup.session.SqlSession;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @program: JVMDome
 * @description: 测试类
 * @author: liuwenhao
 * @create: 2021-01-18 18:55
 **/
public class MyTest {
    @Test
    public void test() {
        DbUtil.selectList();
    }

    @Test
    public void ymlTest() {
        CupUtil cupUtil = new CupUtil();
    }

    @Test
    public void mapperTest() {
        CupUtil cupUtil = new CupUtil();
        SqlSession sqlSession = cupUtil.getSqlSession();
        UsersMapper mapper = sqlSession.getMapper(UsersMapper.class);
        List<Users> userList = mapper.getUserList();
        System.out.println(userList);
    }

    @Test
    public void method(){
        try {
            System.out.println(YmlMapperRead.checkOverload(Class.forName("com.qlu.cup.mapper.PersonMapper")));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}