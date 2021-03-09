package com.qlu.cup.util;

import com.qlu.cup.mapper.BoundSql;

import java.util.HashMap;
import java.util.Map;

/**
 * 普通记号解析器，处理#{}和${}参数
 */
public class GenericTokenParser {

    //有一个开始和结束记号
    private final String openToken;
    private final String closeToken;

    public GenericTokenParser(String openToken, String closeToken) {
        this.openToken = openToken;
        this.closeToken = closeToken;
    }

    public Map<String, Integer> parse(String text, BoundSql boundSql) {
        Map<String, Integer> integerMap = new HashMap<>();
        int intrger = 0;
        StringBuilder builder = new StringBuilder();
        if (text != null && text.length() > 0) {
            char[] src = text.toCharArray();
            int offset = 0;
            int start = text.indexOf(openToken, offset);
            while (start > -1) {
                if (start > 0 && src[start - 1] == '\\') {
                    builder.append(src, offset, start - offset - 1).append(openToken);
                    offset = start + openToken.length();
                } else {
                    int end = text.indexOf(closeToken, start);
                    if (end == -1) {
                        builder.append(src, offset, src.length - offset);
                        offset = src.length;
                    } else {
                        builder.append(src, offset, start - offset);
                        offset = start + openToken.length();
                        String content = new String(src, offset, end - offset);
                        integerMap.put(content, ++intrger);
                        builder.append("?");
                        offset = end + closeToken.length();
                    }
                }
                start = text.indexOf(openToken, offset);
            }
            if (offset < src.length) {
                builder.append(src, offset, src.length - offset);
            }
        }
        boundSql.setSql(builder.toString());
        return integerMap;
    }
}
