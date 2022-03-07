package io.zero88.rsql.jooq.query;

import java.util.UUID;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.meta.h2.information_schema.tables.Tables;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.utils.Strings;
import io.zero88.rsql.jooq.JooqRSQLParser;
import io.zero88.rsql.jooq.JooqRSQLQueryContext;

public class JooqRSQLQueryTest {

    private JooqRSQLParser parser;
    private DSLContext dsl;

    @BeforeEach
    public void before() {
        dsl    = DSL.using("jdbc:h2:mem:dbh2mem-" + UUID.randomUUID());
        parser = JooqRSQLParser.DEFAULT;
    }

    @Test
    public void test_h2_schema_info() {
        final String q = Tables.TABLES.TABLE_SCHEMA.getName() + "==public" + ";" + Tables.TABLES.TABLE_NAME.getName() +
                         "=exists=t" + " and " + "(" + Tables.TABLES.TABLE_TYPE.getName() + "=in=(xyz,abc)" + "," +
                         Tables.TABLES.TABLE_CLASS.getName() + "=out=(123,456)" + ")";
        final Condition condition = parser.criteria(q, Tables.TABLES);
        System.out.println(q);
        System.out.println(Strings.optimizeMultipleSpace(condition.toString()));
        Assertions.assertEquals("( \"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_SCHEMA\" = 'public' and " +
                                "\"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_NAME\" is not null and ( " +
                                "\"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_TYPE\" in ( 'xyz', 'abc' ) or " +
                                "\"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_CLASS\" not in ( '123', '456' ) ) )",
                                Strings.optimizeMultipleSpace(condition.toString()));
        final JooqFetchCountQuery query = JooqFetchCountQuery.builder()
                                                             .parser(parser)
                                                             .context(JooqRSQLQueryContext.create(dsl, Tables.TABLES))
                                                             .build();
        System.out.println(query.toQuery(q));
        Assertions.assertEquals(0, query.execute(q).intValue());
    }

    @Test
    public void test_h2_exist() {
        final String q = Tables.TABLES.TABLE_SCHEMA.getName() + "==public" + ";" + Tables.TABLES.TABLE_NAME.getName() +
                         "=exists=1" + " and " + "(" + Tables.TABLES.TABLE_TYPE.getName() + "=in=(xyz,abc)" + "," +
                         Tables.TABLES.TABLE_CLASS.getName() + "=out=(123,456)" + ")";
        final Condition condition = parser.criteria(q, Tables.TABLES);
        System.out.println(q);
        System.out.println(Strings.optimizeMultipleSpace(condition.toString()));
        Assertions.assertEquals("( \"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_SCHEMA\" = 'public' and " +
                                "\"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_NAME\" is not null and ( " +
                                "\"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_TYPE\" in ( 'xyz', 'abc' ) or " +
                                "\"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_CLASS\" not in ( '123', '456' ) ) )",
                                Strings.optimizeMultipleSpace(condition.toString()));
        final JooqFetchExistQuery query = JooqFetchExistQuery.builder()
                                                             .context(JooqRSQLQueryContext.create(dsl, Tables.TABLES))
                                                             .build();
        System.out.println(query.toQuery(q));
        Assertions.assertEquals(false, query.execute(q));
    }

}
