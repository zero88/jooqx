package io.zero88.rsql.jooq;

import java.util.UUID;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.meta.h2.information_schema.tables.Tables;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.utils.Strings;
import io.zero88.rsql.jooq.query.JooqFetchCountQuery;
import io.zero88.rsql.jooq.query.JooqFetchExistQuery;

public class JooqRqlQueryTest {

    private JooqRqlParser jooqRqlParser;
    private DSLContext dsl;

    @BeforeEach
    public void before() {
        dsl = DSL.using("jdbc:h2:mem:dbh2mem-" + UUID.randomUUID());
        jooqRqlParser = JooqRqlParser.DEFAULT;
    }

    @Test
    public void test_h2_schema_info() {
        final String query = Tables.TABLE_SCHEMA.getName() + "==public" + ";" + Tables.TABLE_NAME.getName() +
                             "=exists=t" + " and " + "(" + Tables.TABLE_TYPE.getName() + "=in=(xyz,abc)" + "," +
                             Tables.TABLE_CLASS.getName() + "=out=(123,456)" + ")";
        final Condition condition = jooqRqlParser.criteria(query, Tables.TABLES);
        System.out.println(query);
        System.out.println(Strings.optimizeMultipleSpace(condition.toString()));
        Assertions.assertEquals("( \"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_SCHEMA\" = 'public' and " +
                                "\"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_NAME\" is not null and ( " +
                                "\"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_TYPE\" in ( 'xyz', 'abc' ) or " +
                                "\"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_CLASS\" not in ( '123', '456' ) ) )",
                                Strings.optimizeMultipleSpace(condition.toString()));
        Assertions.assertEquals(0, JooqFetchCountQuery.builder()
                                                      .parser(jooqRqlParser)
                                                      .dsl(dsl)
                                                      .table(Tables.TABLES)
                                                      .build()
                                                      .execute(query)
                                                      .intValue());
    }

    @Test
    public void test_h2_exist() {
        final String query = Tables.TABLE_SCHEMA.getName() + "==public" + ";" + Tables.TABLE_NAME.getName() +
                             "=exists=1" + " and " + "(" + Tables.TABLE_TYPE.getName() + "=in=(xyz,abc)" + "," +
                             Tables.TABLE_CLASS.getName() + "=out=(123,456)" + ")";
        final Condition condition = jooqRqlParser.criteria(query, Tables.TABLES);
        System.out.println(query);
        System.out.println(Strings.optimizeMultipleSpace(condition.toString()));
        Assertions.assertEquals("( \"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_SCHEMA\" = 'public' and " +
                                "\"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_NAME\" is not null and ( " +
                                "\"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_TYPE\" in ( 'xyz', 'abc' ) or " +
                                "\"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_CLASS\" not in ( '123', '456' ) ) )",
                                Strings.optimizeMultipleSpace(condition.toString()));
        Assertions.assertEquals(false,
                                JooqFetchExistQuery.builder().dsl(dsl).table(Tables.TABLES).build().execute(query));
    }

}