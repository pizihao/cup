package com.qlu.cup.type;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * JDBC类型枚举
 */
public enum JdbcType {
    //就是包装一下java.sql.Types
    ARRAY(Types.ARRAY),
    BIT(Types.BIT),
    TINYINT(Types.TINYINT),
    SMALLINT(Types.SMALLINT),
    INTEGER(Types.INTEGER),
    BIGINT(Types.BIGINT),
    FLOAT(Types.FLOAT),
    REAL(Types.REAL),
    DOUBLE(Types.DOUBLE),
    NUMERIC(Types.NUMERIC),
    DECIMAL(Types.DECIMAL),
    CHAR(Types.CHAR),
    VARCHAR(Types.VARCHAR),
    LONGVARCHAR(Types.LONGVARCHAR),
    DATE(Types.DATE),
    TIME(Types.TIME),
    TIMESTAMP(Types.TIMESTAMP),
    BINARY(Types.BINARY),
    VARBINARY(Types.VARBINARY),
    LONGVARBINARY(Types.LONGVARBINARY),
    NULL(Types.NULL),
    OTHER(Types.OTHER),
    BLOB(Types.BLOB),
    CLOB(Types.CLOB),
    BOOLEAN(Types.BOOLEAN),
    CURSOR(-10),
    UNDEFINED(Integer.MIN_VALUE + 1000),
    NVARCHAR(Types.NVARCHAR),
    NCHAR(Types.NCHAR),
    NCLOB(Types.NCLOB),
    STRUCT(Types.STRUCT);

    public final int TYPE_CODE;
    private static Map<Integer, JdbcType> codeLookup = new HashMap<Integer, JdbcType>();

    //一开始就将数字对应的枚举型放入hashmap
    static {
        for (JdbcType type : JdbcType.values()) {
            codeLookup.put(type.TYPE_CODE, type);
        }
    }

    JdbcType(int code) {
        this.TYPE_CODE = code;
    }

    public static JdbcType forCode(int code) {
        return codeLookup.get(code);
    }

}
