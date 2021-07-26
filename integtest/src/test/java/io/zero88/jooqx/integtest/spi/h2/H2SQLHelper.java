package io.zero88.jooqx.integtest.spi.h2;

import java.util.stream.Stream;

import org.jooq.SQLDialect;

import io.github.zero88.utils.Strings;
import io.vertx.junit5.VertxTestContext;
import io.zero88.jooqx.JooqDSLProvider;
import io.zero88.jooqx.JooqSQL;
import io.zero88.jooqx.SQLConnectionOption;
import io.zero88.jooqx.SQLTestHelper;
import io.zero88.jooqx.integtest.h2.DefaultCatalog;
import io.zero88.jooqx.integtest.h2.DefaultSchema;

import com.zaxxer.hikari.HikariDataSource;

import lombok.NonNull;

public interface H2SQLHelper extends JooqSQL<DefaultSchema>, SQLTestHelper {

    default void prepareDatabase(VertxTestContext context, JooqSQL<?> jooqSql, SQLConnectionOption connOption,
                                 String... otherDataFiles) {
        HikariDataSource dataSource = this.createDataSource(connOption);
        this.prepareDatabase(context, JooqDSLProvider.create(this.dialect(), dataSource).dsl(),
                             Stream.concat(Stream.of("h2_schema.sql"), Stream.of(otherDataFiles))
                                   .toArray(String[]::new));
        System.out.println(Strings.duplicate("=", 150));
    }

    @Override
    default @NonNull SQLDialect dialect() {
        return SQLDialect.H2;
    }

    @Override
    default DefaultSchema schema() {
        return DefaultCatalog.DEFAULT_CATALOG.DEFAULT_SCHEMA;
    }

}
