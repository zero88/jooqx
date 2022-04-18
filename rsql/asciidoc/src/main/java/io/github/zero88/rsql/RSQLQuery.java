package io.github.zero88.rsql;

import org.jooq.DSLContext;

import io.github.zero88.sample.model.pgsql.tables.Authors;
import io.vertx.docgen.Source;
import io.zero88.rsql.jooq.JooqRSQLParser;
import io.zero88.rsql.jooq.JooqRSQLQueryContext;
import io.zero88.rsql.jooq.query.JooqFetchCountQuery;
import io.zero88.rsql.jooq.query.JooqFetchExistQuery;

@Source
public class RSQLQuery {

    public void fetchExists(DSLContext dsl, Authors authorTable, String query) {
        boolean exists = JooqFetchExistQuery.builder()
                                            .parser(JooqRSQLParser.DEFAULT)
                                            .context(JooqRSQLQueryContext.create(dsl, authorTable))
                                            .build()
                                            .execute(query);
    }

    public void fetchCount(DSLContext dsl, Authors authorTable, String query) {
        int count = JooqFetchCountQuery.builder()
                                       .parser(JooqRSQLParser.DEFAULT)
                                       .context(JooqRSQLQueryContext.create(dsl, authorTable))
                                       .build()
                                       .execute(query);
    }

}
