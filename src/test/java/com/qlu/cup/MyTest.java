package com.qlu.cup;

import com.qlu.cup.cutil.CupUtil;
import com.qlu.cup.cutil.DbUtil;
import org.junit.Test;

/**
 * @program: JVMDome
 * @description: 测试类
 * @author: liuwenhao
 * @create: 2021-01-18 18:55
 **/
public class MyTest {
    @Test
    public void test(){
        DbUtil.selectList();
    }
    
    @Test
    public void ymlTest(){
        final CupUtil cupUtil = new CupUtil();
    }
}