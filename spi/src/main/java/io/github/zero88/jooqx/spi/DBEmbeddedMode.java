package io.github.zero88.jooqx.spi;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jooq.SQLDialect;

/**
 * Database embedded mode: memory or local file
 */
public enum DBEmbeddedMode {

    MEMORY, FILE;

    static final List<SQLDialect> EMBEDDED_SUPPORTED = Arrays.asList(SQLDialect.H2, SQLDialect.DERBY, SQLDialect.HSQLDB,
                                                                     SQLDialect.SQLITE);
    static final List<SQLDialect> MEMORY_SUPPORTED = Collections.emptyList();
    static final List<SQLDialect> FILE_SUPPORTED = Collections.emptyList();

    public static boolean isSupportEmbeddedMode(SQLDialect dialect) {
        return EMBEDDED_SUPPORTED.contains(dialect);
    }

    public static boolean isSupportMemoryMode(SQLDialect dialect) {
        return isSupportEmbeddedMode(dialect) || MEMORY_SUPPORTED.contains(dialect);
    }

    public static boolean isSupportFileMode(SQLDialect dialect) {
        return isSupportEmbeddedMode(dialect) || FILE_SUPPORTED.contains(dialect);
    }

    public static boolean isFile(DBEmbeddedMode mode) {
        return FILE == mode;
    }

    public static boolean isMemory(DBEmbeddedMode mode) {
        return MEMORY == mode;
    }
}
