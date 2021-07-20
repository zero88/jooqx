package io.zero88.jooqx.provider;

import org.jetbrains.annotations.NotNull;
import org.jooq.SQLDialect;

/**
 * Provides SQL dialect
 *
 * @see SQLDialect
 * @since 1.1.0
 */
@FunctionalInterface
public interface HasSQLDialect {

    @NotNull SQLDialect dialect();

}
