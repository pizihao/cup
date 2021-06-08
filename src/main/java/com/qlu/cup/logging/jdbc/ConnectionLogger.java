package com.qlu.cup.logging.jdbc;

import com.qlu.cup.logging.Log;
import com.qlu.cup.util.ExceptionUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * Connection proxy to add logging
 * @author liuwenhao
 */
public final class ConnectionLogger extends BaseJdbcLogger implements InvocationHandler {

    private Connection connection;

    private ConnectionLogger(Connection conn, Log statementLog, int queryStack) {
        super(statementLog, queryStack);
        this.connection = conn;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] params)
            throws Throwable {
        try {
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, params);
            }
            switch (method.getName()) {
                case "prepareStatement":
                case "prepareCall": {
                    if (isDebugEnabled()) {
                        debug(" Preparing: " + removeBreakingWhitespace((String) params[0]), true);
                    }
                    PreparedStatement stmt = (PreparedStatement) method.invoke(connection, params);
                    stmt = PreparedStatementLogger.newInstance(stmt, statementLog, queryStack);
                    return stmt;
                }
                case "createStatement": {
                    Statement stmt = (Statement) method.invoke(connection, params);
                    stmt = StatementLogger.newInstance(stmt, statementLog, queryStack);
                    return stmt;
                }
                default:
                    return method.invoke(connection, params);
            }
        } catch (Throwable t) {
            throw ExceptionUtil.unwrapThrowable(t);
        }
    }

    /**
     * Creates a logging version of a connection
     */
    public static Connection newInstance(Connection conn, Log statementLog, int queryStack) {
        InvocationHandler handler = new ConnectionLogger(conn, statementLog, queryStack);
        ClassLoader cl = Connection.class.getClassLoader();
        return (Connection) Proxy.newProxyInstance(cl, new Class[]{Connection.class}, handler);
    }

    /**
     * return the wrapped connection
     */
    public Connection getConnection() {
        return connection;
    }

}
