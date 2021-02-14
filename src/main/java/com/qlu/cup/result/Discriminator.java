package com.qlu.cup.result;

import com.qlu.cup.bind.Configuration;

import java.util.Collections;
import java.util.Map;

/**
 * 鉴别器
 * 有时一个查询也许返回很多不同数据类型的结果集。
 */
public class Discriminator {

    private ResultMapping resultMapping;
    private Map<String, String> discriminatorMap;

    Discriminator() {
    }

    public static class Builder {
        private Discriminator discriminator = new Discriminator();

        public Builder(Configuration configuration, ResultMapping resultMapping, Map<String, String> discriminatorMap) {
            discriminator.resultMapping = resultMapping;
            discriminator.discriminatorMap = discriminatorMap;
        }

        public Discriminator build() {
            assert discriminator.resultMapping != null;
            assert discriminator.discriminatorMap != null;
            assert !discriminator.discriminatorMap.isEmpty();
            //lock down map
            discriminator.discriminatorMap = Collections.unmodifiableMap(discriminator.discriminatorMap);
            return discriminator;
        }
    }

    public ResultMapping getResultMapping() {
        return resultMapping;
    }

    public Map<String, String> getDiscriminatorMap() {
        return discriminatorMap;
    }

    public String getMapIdFor(String s) {
        return discriminatorMap.get(s);
    }

}
