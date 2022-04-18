package io.github.zero88.integtest.jooqx.h2;

import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.jooq.SQLDialect;

import io.github.zero88.jooqx.JooqDSLProvider;
import io.github.zero88.jooqx.JooqSQL;
import io.github.zero88.jooqx.SQLConnectionOption;
import io.github.zero88.jooqx.SQLTestHelper;
import io.github.zero88.jooqx.spi.jdbc.JDBCErrorConverterProvider;
import io.github.zero88.sample.model.h2.DefaultCatalog;
import io.github.zero88.sample.model.h2.DefaultSchema;
import io.github.zero88.utils.Strings;
import io.vertx.junit5.VertxTestContext;

import com.zaxxer.hikari.HikariDataSource;

public interface H2SQLHelper extends JooqSQL<DefaultSchema>, SQLTestHelper, JDBCErrorConverterProvider {

    default void prepareDatabase(VertxTestContext context, JooqSQL<?> jooqSql, SQLConnectionOption connOption,
                                 String... otherDataFiles) {
        HikariDataSource dataSource = this.createDataSource(connOption);
        this.prepareDatabase(context, JooqDSLProvider.create(this.dialect(), dataSource).dsl(),
                             Stream.concat(Stream.of("h2_schema.sql"), Stream.of(otherDataFiles))
                                   .toArray(String[]::new));
        System.out.println(Strings.duplicate("=", 150));
    }

    @Override
    default @NotNull SQLDialect dialect() {
        return SQLDialect.H2;
    }

    @Override
    default DefaultSchema schema() {
        return DefaultCatalog.DEFAULT_CATALOG.DEFAULT_SCHEMA;
    }

}
