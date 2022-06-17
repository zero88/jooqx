package io.github.zero88.jooqx;

public final class Utils {

    private Utils() { }

    public static boolean isPool(Object sqlClient) {
        return isSpecificClient("io.vertx.sqlclient.Pool", sqlClient);
    }

    public static boolean isConnection(Object sqlClient) {
        return isSpecificClient("io.vertx.sqlclient.SqlClient", sqlClient);
    }

    public static boolean isJDBC(Object sqlClient) {
        return isSpecificClient("io.vertx.jdbcclient.JDBCPool", sqlClient);
    }

    public static boolean isLegacyJDBC(Object sqlClient) {
        return isSpecificClient("io.vertx.ext.jdbc.JDBCClient", sqlClient);
    }

    private static boolean isSpecificClient(String fqnSqlClassName, Object sqlClient) {
        if (sqlClient == null) {
            return false;
        }
        Class<?> poolClass = loadClass(fqnSqlClassName);
        return poolClass != null && poolClass.isAssignableFrom(sqlClient.getClass());
    }

    private static Class<?> loadClass(String fqnClassName) {
        try {
            return Class.forName(fqnClassName);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

}
