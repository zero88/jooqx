package io.github.zero88.jooqx;

import javax.sql.DataSource;

import org.jetbrains.annotations.NotNull;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;

/**
 * Provides jOOQ DSL Context
 *
 * @see DSLContext
 * @since 2.0.0
 */
@FunctionalInterface
public interface JooqDSLProvider {

    /**
     * Defines jOOQ {@code DSL Context}
     *
     * @return the DSL context
     * @see DSLContext
     */
    @NotNull DSLContext dsl();

    @NotNull
    static JooqDSLProvider create(SQLDialect dialect) {
        return create(new DefaultConfiguration().set(dialect));
    }

    @NotNull
    static JooqDSLProvider create(SQLDialect dialect, DataSource dataSource) {
        return create(new DefaultConfiguration().derive(dialect).derive(dataSource));
    }

    static JooqDSLProvider create(Configuration configuration) {
        return () -> DSL.using(configuration);
    }

}
