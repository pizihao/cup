package com.qlu.cup.result;

import java.util.ArrayList;
import java.util.List;

/**
 * 默认结果处理器
 */
public class DefaultResultHandler implements ResultProcessor {

    private final List<Object> list;

    public DefaultResultHandler() {
        list = new ArrayList<Object>();
    }

    @Override
    public void handleResult(ResultContext context) {
        list.add(context.getResultObject());
    }

    public List<Object> getResultList() {
        return list;
    }

}
