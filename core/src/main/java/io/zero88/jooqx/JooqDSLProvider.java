package io.zero88.jooqx;

import javax.sql.DataSource;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;

/**
 * Provides jOOQ DSL Context
 *
 * @see DSLContext
 * @since 1.1.0
 */
@FunctionalInterface
public interface JooqDSLProvider {

    /**
     * Defines jOOQ {@code DSL context}.
     *
     * @return the DSL context
     * @see DSLContext
     */
    DSLContext dsl();

    @NotNull
    static JooqDSLProvider create(SQLDialect dialect) {
        return () -> DSL.using(new DefaultConfiguration().set(dialect));
    }

    @NotNull
    static JooqDSLProvider create(SQLDialect dialect, DataSource dataSource) {
        return () -> DSL.using(new DefaultConfiguration().derive(dialect).derive(dataSource));
    }

}
