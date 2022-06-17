package io.github.zero88.jooqx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Param;
import org.jooq.Parameter;
import org.jooq.Routine;
import org.jooq.Schema;
import org.jooq.impl.AbstractRoutine;
import org.jooq.impl.DSL;

import io.github.zero88.jooqx.provider.HasSQLDialect;
import io.github.zero88.utils.Strings;
import io.vertx.junit5.VertxTestContext;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * SQL test helper for initializing the test database schema via {@code HikariCP} and plain {@code jOOQ}
 *
 * @param <S> Type of schema
 * @see Schema
 */
public interface JooqSQL<S extends Schema> extends JooqDSLProvider, HasSQLDialect {

    /**
     * Get test database schema
     *
     * @return test schema
     */
    S schema();

    @Override
    default @NotNull DSLContext dsl() {
        return JooqDSLProvider.create(dialect()).dsl();
    }

    /**
     * Create Hikari data source
     *
     * @param option sql connection options
     * @return Hikari data source
     */
    default HikariDataSource createDataSource(SQLConnectionOption option) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(option.getJdbcUrl());
        hikariConfig.setUsername(option.getUser());
        hikariConfig.setPassword(option.getPassword());
        hikariConfig.setDriverClassName(option.getDriverClassName());
        return new HikariDataSource(hikariConfig);
    }

    /**
     * Close Hikari datasource
     *
     * @param dataSource datasource
     */
    default void closeDataSource(HikariDataSource dataSource) {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    /**
     * Prepare database when tear-up test case
     *
     * @param context Vertx test context
     * @param dsl     dsl
     * @param files   SQL files
     */
    default void prepareDatabase(@NotNull VertxTestContext context, @NotNull DSLContext dsl, @NotNull String... files) {
        Arrays.stream(files).map(file -> {
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(file))))) {
                return reader.lines().collect(Collectors.joining("\n"));
            } catch (IOException | NullPointerException e) {
                context.failNow(e);
                return null;
            }
        }).filter(Objects::nonNull).forEach(sql -> dsl.execute(DSL.sql(sql)));
        context.completeNow();
    }

    static void printJooqRoutine(DSLContext dsl, Routine<?> routine) {
        final String inParams = routine.getInParameters()
                                       .stream()
                                       .map(p -> extractParam(p, (Param<?>) routine.getInValue(p)))
                                       .collect(Collectors.joining(","));
        final String outParams = routine.getOutParameters()
                                        .stream()
                                        .map(p -> extractParam(p, null))
                                        .collect(Collectors.joining(","));
        final String returnParams = Optional.ofNullable(routine.getReturnParameter())
                                            .map(p -> extractParam(p, null))
                                            .orElse(null);
        final Field<?> asField = ((AbstractRoutine<?>) routine).asField();
        System.out.println(Strings.duplicate("-", 100));
        System.out.println("RENDER:                         " + dsl.render(routine));
        System.out.println("RENDER Inlined:                 " + dsl.renderInlined(routine));
        System.out.println("RENDER Named Params:            " + dsl.renderNamedParams(routine));
        System.out.println("PARAMETERS:                     " + routine.getParameters().size());
        System.out.println("IN PARAMETERS:                  " + inParams);
        System.out.println("OUT PARAMETERS:                 " + outParams);
        System.out.println("RETURN PARAMETERS:              " + returnParams);
        System.out.println(Strings.duplicate("-", 100));
        if (Objects.nonNull(asField)) {
            System.out.println("RENDER BY_FIELD:                " + dsl.render(asField));
            System.out.println("RENDER BY_FIELD Inlined:        " + dsl.renderInlined(asField));
            System.out.println("RENDER BY_FIELD Named Params:   " + dsl.renderNamedParams(asField));
        }
        System.out.println(Strings.duplicate("=", 150));
    }

    static String extractParam(Parameter<?> p, Param<?> inValue) {
        StringBuilder builder = new StringBuilder();
        builder.append(p.getName());
        builder.append("::").append("DataType[").append(p.getDataType().getType()).append("]");
        builder.append("::").append("SQLType[").append(p.getDataType().getSQLType()).append("]");
        builder.append("::").append("Default[").append(p.isDefaulted()).append("]");
        builder.append("::").append("Unnamed[").append(p.isUnnamed()).append("]");
        if (Objects.nonNull(inValue)) {
            builder.append("::")
                   .append("Value[")
                   .append(inValue.getValue())
                   .append("-")
                   .append(Optional.ofNullable(inValue.getValue()).map(Object::getClass).orElse(null))
                   .append("]");
        }
        return builder.toString();
    }

    static void printJooqRoutineResult(Routine<?> proc) {
        System.out.println("RETURN VALUE:           " + proc.getReturnValue() + "==" +
                           Optional.ofNullable(proc.getReturnValue()).map(Object::getClass).orElse(null));
        System.out.println("RESULTS:                " + proc.getResults() + "==" + proc.getResults().size());
        System.out.println("RESULTS OR ROWS:        " + proc.getResults().resultsOrRows() + "==" +
                           proc.getResults().resultsOrRows().size());
    }

}
