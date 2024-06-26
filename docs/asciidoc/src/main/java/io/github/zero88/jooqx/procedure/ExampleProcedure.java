package io.github.zero88.jooqx.procedure;

import java.util.Arrays;
import java.util.Objects;

import org.jooq.Field;
import org.jooq.Record;

import io.github.zero88.jooqx.Jooqx;
import io.github.zero88.sample.model.pgsql.Tables;
import io.github.zero88.sample.model.pgsql.routines.Add;
import io.github.zero88.sample.model.pgsql.routines.Dup;
import io.github.zero88.sample.model.pgsql.tables.records.AuthorsRecord;
import io.vertx.docgen.Source;

@Source
class ExampleProcedure {

    void routine(Jooqx jooqx) {
        Add addFn = new Add();                      // <1>
        addFn.set__1(5);                            // <2>
        addFn.set__2(10);
        jooqx.routine(addFn).onSuccess(output -> {  // <3>
            assert 15 == output;
        }).onFailure(Throwable::printStackTrace);   // <4>
    }

    void routineResult(Jooqx jooqx) {
        Dup dup = new Dup();                                                                // <1>
        // Routine has 1 in-parameter and 2 out-parameters
        dup.set__1(5);                                                                      // <2>
        jooqx.routineResult(dup).onSuccess(rr -> {
            Record record = rr.getRecord();                                                 // <3>
            Field<?>[] outputFields = rr.getOutputFields();                                 // <4>
            assert Objects.equals(Arrays.toString(outputFields), "[\"f1\", \"f2\"]");
            assert record.fields().length == 2;
            assert 5 == (Integer) record.get(outputFields[0]);
            assert Objects.equals(record.get(outputFields[1]), "5 is text");
            assert Objects.equals(record.toString(), """
                +----+----------+
                |  f1|f2        |
                +----+----------+
                |  *5|*5 is text|
                +----+----------+
                """);                                                                      // <5>
        }).onFailure(Throwable::printStackTrace);                                          // <6>
    }

    void routineResultSetPgSQL(Jooqx jooqx) {
        jooqx.fetchMany(dsl -> dsl.selectFrom(Tables.FIND_AUTHORS.call("Sc"))).onSuccess(rows -> {
            assert Objects.equals(rows.toString(), """
                [+----+---------------------+-------+
                |  id|name                 |country|
                +----+---------------------+-------+
                |  *2|*F. Scott. Fitzgerald|*USA   |
                +----+---------------------+-------+
                , +----+----------------+-------+
                |  id|name            |country|
                +----+----------------+-------+
                |  *4|*Scott Hanselman|*USA   |
                +----+----------------+-------+
                ]
                """);
            AuthorsRecord rec1 = rows.get(0);
            assert rec1.getId() == 2;
            assert Objects.equals(rec1.getName(), "F. Scott. Fitzgerald");
        }).onFailure(Throwable::printStackTrace);
    }

}
