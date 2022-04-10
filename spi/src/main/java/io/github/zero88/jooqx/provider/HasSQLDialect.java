package io.github.zero88.jooqx.provider;

import org.jetbrains.annotations.NotNull;
import org.jooq.SQLDialect;

/**
 * Provides SQL dialect
 *
 * @see SQLDialect
 * @since 2.0.0
 */
@FunctionalInterface
public interface HasSQLDialect {

    @NotNull SQLDialect dialect();

}
