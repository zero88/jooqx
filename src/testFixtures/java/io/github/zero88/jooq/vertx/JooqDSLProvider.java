package io.github.zero88.jooq.vertx;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;

import lombok.NonNull;

public interface JooqDSLProvider {

    @NonNull SQLDialect dialect();

    default DSLContext dsl() {
        return DSL.using(new DefaultConfiguration().set(dialect()));
    }

    default DSLContext dsl(DataSource dataSource) {
        return DSL.using(new DefaultConfiguration().derive(dialect()).derive(dataSource));
    }

}
