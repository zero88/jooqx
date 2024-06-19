package io.github.zero88.jooqx;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class Utils {

    private Utils()                                  { }

    public static String brackets(Object any)        { return "[" + repr(any) + "]"; }

    public static String brackets(Object... objects) { return brackets("::", objects); }

    public static String brackets(String delimiter, Object... objects) {
        return brackets(Arrays.stream(objects).map(Utils::repr).collect(Collectors.joining(delimiter)));
    }

    public static String parentheses(Object any)        { return "(" + repr(any) + ")"; }

    public static String parentheses(Object... objects) { return parentheses("::", objects); }

    public static String parentheses(String delimiter, Object... objects) {
        return parentheses(Arrays.stream(objects).map(Utils::repr).collect(Collectors.joining(delimiter)));
    }

    public static boolean isPool(Object sqlClient) {
        return isAssignableFromClass("io.vertx.sqlclient.Pool", sqlClient);
    }

    public static boolean isConnection(Object sqlClient) {
        return isAssignableFromClass("io.vertx.sqlclient.SqlClient", sqlClient);
    }

    public static boolean isJDBC(Object sqlClient) {
        return isAssignableFromClass("io.vertx.jdbcclient.JDBCPool", sqlClient);
    }

    public static boolean isLegacyJDBC(Object sqlClient) {
        return isAssignableFromClass("io.vertx.ext.sql.SQLClient", sqlClient);
    }

    public static boolean isPgPool(Object sqlClient) {
        return isAssignableFromClass("io.vertx.pgclient.PgPool", sqlClient);
    }

    public static boolean isPgConn(Object sqlClient) {
        return isAssignableFromClass("io.vertx.pgclient.PgConnection", sqlClient);
    }

    public static boolean isContextConverter(Object converter) {
        return isAssignableFromClass("org.jooq.ContextConverter", converter);
    }

    public static String classRepr(Class<?> clazz) {
        return clazz.getName().replaceAll("\\$\\$Lambda\\$.+", "");
    }

    private static String repr(Object o) {
        if (o == null) { return null; }
        return o instanceof Class ? classRepr((Class<?>) o) : o.toString();
    }

    private static boolean isAssignableFromClass(String fqnParentClassName, Object object) {
        if (object == null) {
            return false;
        }
        Class<?> parentClass = loadClass(fqnParentClassName);
        return parentClass != null && parentClass.isAssignableFrom(object.getClass());
    }

    private static Class<?> loadClass(String fqnClassName) {
        try {
            return Class.forName(fqnClassName);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

}
