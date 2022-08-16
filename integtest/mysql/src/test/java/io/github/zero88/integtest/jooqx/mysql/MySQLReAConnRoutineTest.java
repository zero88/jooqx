package io.github.zero88.integtest.jooqx.mysql;

import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.github.zero88.jooqx.BlockQuery;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.JooqSQL;
import io.github.zero88.jooqx.spi.mysql.MySQLConnProvider;
import io.github.zero88.jooqx.spi.mysql.MySQLJooqxTest;
import io.github.zero88.sample.model.mysql.Tables;
import io.github.zero88.sample.model.mysql.routines.CountAuthorByCountry;
import io.github.zero88.sample.model.mysql.routines.Hello;
import io.github.zero88.sample.model.mysql.routines.RemoveAuthor;
import io.github.zero88.sample.model.mysql.routines.SelectBooksByAuthor;
import io.github.zero88.sample.model.mysql.tables.Books;
import io.github.zero88.sample.model.mysql.tables.records.BooksRecord;
import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.mysqlclient.MySQLConnection;
import io.vertx.mysqlclient.MySQLSetOption;

class MySQLReAConnRoutineTest extends MySQLJooqxTest<MySQLConnection> implements MySQLConnProvider, MySQLHelper {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, connOpt, "mysql_data/book_author.sql");
    }

    @Test
    void test_fn_returns_value(VertxTestContext ctx) {
        final Checkpoint cp = ctx.checkpoint();
        final Hello routine = new Hello();
        routine.setS("hi");
        jooqx.routine(routine).onSuccess(out -> ctx.verify(() -> {
            Assertions.assertEquals("Hello, hi!", out);
            cp.flag();
        })).onFailure(ctx::failNow);
    }

    @Test
    @Disabled("Not yet supported out params in mysql reactive")
    void test_proc_returns_outParams(VertxTestContext ctx) {
        final Checkpoint cp = ctx.checkpoint();
        final CountAuthorByCountry byCountry = new CountAuthorByCountry();
        byCountry.setCountry("USA");
        JooqSQL.printJooqRoutine(jooqx.dsl(), byCountry);

        jooqx.sqlClient()
             .setOption(MySQLSetOption.MYSQL_OPTION_MULTI_STATEMENTS_ON)
             .onFailure(ctx::failNow)
             .flatMap(unused -> jooqx.sqlQuery(
                 dsl -> "call `test`.`count_author_by_country` ('USA', @count); SELECT @count;",
                 DSLAdapter.fetchOne(DSL.field("count", String.class))))
             .onSuccess(record -> ctx.verify(() -> {
                 System.out.println(record);
                 cp.flag();
             }))
             .onFailure(ctx::failNow);
    }

    @Test
    void test_proc_returns_resultSet(VertxTestContext ctx) {
        final Checkpoint cp = ctx.checkpoint();
        final SelectBooksByAuthor proc = new SelectBooksByAuthor();
        proc.setAuthor("Jane Austen");
        JooqSQL.printJooqRoutine(jooqx.dsl(), proc);
        jooqx.routineResultSet(proc, DSLAdapter.fetchMany(Books.BOOKS)).onSuccess(records -> ctx.verify(() -> {
            Assertions.assertEquals(1, records.size());
            BooksRecord rec = records.get(0);
            Assertions.assertEquals(6, rec.getId());
            Assertions.assertEquals("Pride and Prejudice", rec.getTitle());
            cp.flag();
        })).onFailure(ctx::failNow);
    }

    @Test
    void test_proc_returns_void(VertxTestContext ctx) {
        final Checkpoint cp = ctx.checkpoint(2);
        final RemoveAuthor proc = new RemoveAuthor();
        final String authorName = "J.D. Salinger";
        proc.setAuthorName(authorName);
        jooqx.routine(proc)
             .flatMap(r -> jooqx.execute(dsl -> dsl.selectOne()
                                                   .whereExists(dsl.selectFrom(Tables.AUTHORS)
                                                                   .where(Tables.AUTHORS.NAME.eq(authorName))),
                                         DSLAdapter.fetchExists()))
             .onSuccess(r -> ctx.verify(() -> {
                 Assertions.assertFalse(r);
                 cp.flag();
             }))
             .flatMap(r -> jooqx.execute(dsl -> dsl.select().from(Tables.BOOKS), DSLAdapter.fetchMany(Tables.BOOKS)))
             .onSuccess(records -> ctx.verify(() -> {
                 Assertions.assertEquals(4, records.size());
                 cp.flag();
             }))
             .onFailure(ctx::failNow);
    }

    @Test
    @Disabled("Unsupported: MySQL unable execute multiple queries?")
    /**
     * io.vertx.mysqlclient.MySQLException: You have an error in your SQL syntax; check the manual that corresponds
     * to your MySQL server version for the right syntax to use near 'select `test`.`books`.`id`, `test`.`books`
     * .`title` from `test`.`books`' at line 1
     *
     * 	at io.vertx.mysqlclient.impl.codec.CommandCodec.handleErrorPacketPayload(CommandCodec.java:131)
     * 	at io.vertx.mysqlclient.impl.codec.PrepareStatementCodec.decodePayload(PrepareStatementCodec.java:54)
     * 	at io.vertx.mysqlclient.impl.codec.MySQLDecoder.decodePacket(MySQLDecoder.java:77)
     * 	at io.vertx.mysqlclient.impl.codec.MySQLDecoder.decode(MySQLDecoder.java:66)
     * 	at io.netty.handler.codec.ByteToMessageDecoder.decodeRemovalReentryProtection(ByteToMessageDecoder.java:507)
     * 	at io.netty.handler.codec.ByteToMessageDecoder.callDecode(ByteToMessageDecoder.java:446)
     * 	at io.netty.handler.codec.ByteToMessageDecoder.channelRead(ByteToMessageDecoder.java:276)
     * 	at io.netty.channel.CombinedChannelDuplexHandler.channelRead(CombinedChannelDuplexHandler.java:251)
     * 	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379)
     * 	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365)
     * 	at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:357)
     * 	at io.netty.channel.DefaultChannelPipeline$HeadContext.channelRead(DefaultChannelPipeline.java:1410)
     * 	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379)
     * 	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365)
     * 	at io.netty.channel.DefaultChannelPipeline.fireChannelRead(DefaultChannelPipeline.java:919)
     * 	at io.netty.channel.nio.AbstractNioByteChannel$NioByteUnsafe.read(AbstractNioByteChannel.java:166)
     * 	at io.netty.channel.nio.NioEventLoop.processSelectedKey(NioEventLoop.java:722)
     * 	at io.netty.channel.nio.NioEventLoop.processSelectedKeysOptimized(NioEventLoop.java:658)
     * 	at io.netty.channel.nio.NioEventLoop.processSelectedKeys(NioEventLoop.java:584)
     * 	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:496)
     * 	at io.netty.util.concurrent.SingleThreadEventExecutor$4.run(SingleThreadEventExecutor.java:986)
     * 	at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74)
     * 	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
     * 	at java.base/java.lang.Thread.run(Thread.java:829)
     */
    void test_select_block(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        jooqx.sqlClient()
             .setOption(MySQLSetOption.MYSQL_OPTION_MULTI_STATEMENTS_ON)
             .onFailure(ctx::failNow)
             .flatMap(ignore -> jooqx.block(dsl -> BlockQuery.create()
                                                             .add(dsl.select().from(schema().AUTHORS),
                                                                  DSLAdapter.fetchMany(schema().AUTHORS))
                                                             .add(dsl.select().from(schema().BOOKS),
                                                                  DSLAdapter.fetchMany(schema().BOOKS))))
             .onSuccess(r -> ctx.verify(() -> {
                 flag.flag();
             }))
             .onFailure(ctx::failNow);
    }

}
