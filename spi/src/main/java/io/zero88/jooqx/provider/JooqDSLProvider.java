package io.zero88.jooqx.provider;

import javax.sql.DataSource;

import org.jetbrains.annotations.NonNls;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;

/**
 * Provide jOOQ DSL
 */
public interface JooqDSLProvider {

    @NonNls SQLDialect dialect();

    @NonNls
    default DSLContext dsl() {
        return DSL.using(new DefaultConfiguration().set(dialect()));
    }

    @NonNls
    default DSLContext dsl(DataSource dataSource) {
        return DSL.using(new DefaultConfiguration().derive(dialect()).derive(dataSource));
    }

}
