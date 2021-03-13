/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.zero88.jooqx.reactivex;

import io.vertx.reactivex.RxHelper;
import io.vertx.reactivex.ObservableHelper;
import io.vertx.reactivex.FlowableHelper;
import io.vertx.reactivex.impl.AsyncResultMaybe;
import io.vertx.reactivex.impl.AsyncResultSingle;
import io.vertx.reactivex.impl.AsyncResultCompletable;
import io.vertx.reactivex.WriteStreamObserver;
import io.vertx.reactivex.WriteStreamSubscriber;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.vertx.core.Handler;
import io.vertx.core.AsyncResult;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import io.vertx.lang.rx.RxGen;
import io.vertx.lang.rx.TypeArg;
import io.vertx.lang.rx.MappingIterator;

/**
 * Represents for an executor that executes <code>jOOQ query</code> on <code>Vertx reactive SQL client</code>
 * connection
 * <p>
 * <p/>
 * NOTE: This class has been automatically generated from the {@link io.zero88.jooqx.ReactiveJooqx original} non
 * RX-ified interface using Vert.x codegen.
 */

@RxGen(io.zero88.jooqx.ReactiveJooqx.class)
public class ReactiveJooqx<S extends io.vertx.sqlclient.SqlClient> {

    @Override
    public String toString() {
        return delegate.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReactiveJooqx that = (ReactiveJooqx) o;
        return delegate.equals(that.delegate);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    public static final TypeArg<ReactiveJooqx> __TYPE_ARG = new TypeArg<>(
        obj -> new ReactiveJooqx((io.zero88.jooqx.ReactiveJooqx) obj), ReactiveJooqx::getDelegate);

    private final io.zero88.jooqx.ReactiveJooqx<S> delegate;
    public final TypeArg<S> __typeArg_0;

    public ReactiveJooqx(io.zero88.jooqx.ReactiveJooqx delegate) {
        this.delegate = delegate;
        this.__typeArg_0 = TypeArg.unknown();
    }

    public ReactiveJooqx(Object delegate, TypeArg<S> typeArg_0) {
        this.delegate = (io.zero88.jooqx.ReactiveJooqx) delegate;
        this.__typeArg_0 = typeArg_0;
    }

    public io.zero88.jooqx.ReactiveJooqx getDelegate() {
        return delegate;
    }

    public io.vertx.reactivex.core.Vertx vertx() {
        io.vertx.reactivex.core.Vertx ret = io.vertx.reactivex.core.Vertx.newInstance(
            (io.vertx.core.Vertx) delegate.vertx());
        return ret;
    }



    public S sqlClient() {
        S ret = (S) __typeArg_0.wrap(delegate.sqlClient());
        return ret;
    }

    public io.zero88.jooqx.reactivex.ReactiveJooqx<S> transaction() {
        io.zero88.jooqx.reactivex.ReactiveJooqx<S> ret = io.zero88.jooqx.reactivex.ReactiveJooqx.newInstance(
            (io.zero88.jooqx.ReactiveJooqx) delegate.transaction(), __typeArg_0);
        return ret;
    }

    public org.jooq.DSLContext dsl() {
        org.jooq.DSLContext ret = delegate.dsl();
        return ret;
    }

    public io.zero88.jooqx.ReactiveSQLPreparedQuery preparedQuery() {
        io.zero88.jooqx.ReactiveSQLPreparedQuery ret = delegate.preparedQuery();
        return ret;
    }

    public io.zero88.jooqx.SQLErrorConverter<? extends java.lang.Throwable, ? extends java.lang.RuntimeException> errorConverter() {
        io.zero88.jooqx.SQLErrorConverter<? extends java.lang.Throwable, ? extends java.lang.RuntimeException> ret
            = delegate.errorConverter();
        return ret;
    }

    public <T extends org.jooq.TableLike<?>, R> void execute(org.jooq.Query query,
                               io.zero88.jooqx.adapter.SQLResultAdapter<io.vertx.sqlclient.RowSet<io.vertx.sqlclient.Row>, io.zero88.jooqx.ReactiveSQLResultConverter, T, R> resultAdapter,
                               Handler<AsyncResult<R>> handler) {
        delegate.execute(query, resultAdapter, handler);
    }

    public <T extends org.jooq.TableLike<?>, R> void execute(org.jooq.Query query,
                               io.zero88.jooqx.adapter.SQLResultAdapter<io.vertx.sqlclient.RowSet<io.vertx.sqlclient.Row>, io.zero88.jooqx.ReactiveSQLResultConverter, T, R> resultAdapter) {
        execute(query, resultAdapter, ar -> { });
    }

    public <T extends org.jooq.TableLike<?>, R> io.reactivex.Maybe<R> rxExecute(org.jooq.Query query,
                                                  io.zero88.jooqx.adapter.SQLResultAdapter<io.vertx.sqlclient.RowSet<io.vertx.sqlclient.Row>, io.zero88.jooqx.ReactiveSQLResultConverter, T, R> resultAdapter) {
        return AsyncResultMaybe.toMaybe($handler -> {
            execute(query, resultAdapter, $handler);
        });
    }

    public void batch(org.jooq.Query query, io.zero88.jooqx.BindBatchValues bindBatchValues,
                      Handler<AsyncResult<io.zero88.jooqx.BatchResult>> handler) {
        delegate.batch(query, bindBatchValues, handler);
    }

    public void batch(org.jooq.Query query, io.zero88.jooqx.BindBatchValues bindBatchValues) {
        batch(query, bindBatchValues, ar -> { });
    }

    public io.reactivex.Single<io.zero88.jooqx.BatchResult> rxBatch(org.jooq.Query query,
                                                                    io.zero88.jooqx.BindBatchValues bindBatchValues) {
        return AsyncResultSingle.toSingle($handler -> {
            batch(query, bindBatchValues, $handler);
        });
    }

    public <T extends org.jooq.TableLike<?>, R> void batch(org.jooq.Query query, io.zero88.jooqx.BindBatchValues bindBatchValues,
                             io.zero88.jooqx.adapter.SelectListResultAdapter<io.vertx.sqlclient.RowSet<io.vertx.sqlclient.Row>, io.zero88.jooqx.ReactiveSQLBatchConverter, T, R> adapter,
                             Handler<AsyncResult<io.zero88.jooqx.BatchReturningResult<R>>> handler) {
        delegate.batch(query, bindBatchValues, adapter, handler);
    }

    public <T extends org.jooq.TableLike<?>, R> void batch(org.jooq.Query query, io.zero88.jooqx.BindBatchValues bindBatchValues,
                             io.zero88.jooqx.adapter.SelectListResultAdapter<io.vertx.sqlclient.RowSet<io.vertx.sqlclient.Row>, io.zero88.jooqx.ReactiveSQLBatchConverter, T, R> adapter) {
        batch(query, bindBatchValues, adapter, ar -> { });
    }

    public <T extends org.jooq.TableLike<?>, R> io.reactivex.Single<io.zero88.jooqx.BatchReturningResult<R>> rxBatch(org.jooq.Query query,
                                                                                       io.zero88.jooqx.BindBatchValues bindBatchValues,
                                                                                       io.zero88.jooqx.adapter.SelectListResultAdapter<io.vertx.sqlclient.RowSet<io.vertx.sqlclient.Row>, io.zero88.jooqx.ReactiveSQLBatchConverter, T, R> adapter) {
        return AsyncResultSingle.toSingle($handler -> {
            batch(query, bindBatchValues, adapter, $handler);
        });
    }

    public <X> void run(Function<io.zero88.jooqx.reactivex.ReactiveJooqx<S>, io.vertx.core.Future<X>> function,
                        Handler<AsyncResult<X>> handler) {
        delegate.run(new Function<io.zero88.jooqx.ReactiveJooqx<S>, io.vertx.core.Future<X>>() {
            public io.vertx.core.Future<X> apply(io.zero88.jooqx.ReactiveJooqx<S> arg) {
                io.vertx.core.Future<X> ret = function.apply(
                    io.zero88.jooqx.reactivex.ReactiveJooqx.newInstance((io.zero88.jooqx.ReactiveJooqx) arg,
                                                                        __typeArg_0));
                return ret;
            }
        }, handler);
    }

    public <X> void run(Function<io.zero88.jooqx.reactivex.ReactiveJooqx<S>, io.vertx.core.Future<X>> function) {
        run(function, ar -> { });
    }

    public <X> io.reactivex.Single<X> rxRun(
        Function<io.zero88.jooqx.reactivex.ReactiveJooqx<S>, io.vertx.core.Future<X>> function) {
        return AsyncResultSingle.toSingle($handler -> {
            run(function, $handler);
        });
    }

    public <X> void run(
        io.reactivex.functions.Function<io.zero88.jooqx.reactivex.ReactiveJooqx<S>, io.reactivex.Single<X>> function,
        Handler<AsyncResult<X>> handler) {
        delegate.run(new Function<io.zero88.jooqx.ReactiveJooqx<S>, io.vertx.core.Future<X>>() {
            public io.vertx.core.Future<X> apply(io.zero88.jooqx.ReactiveJooqx<S> arg) {
                io.reactivex.Single<X> ret;
                try {
                    ret = function.apply(
                        io.zero88.jooqx.reactivex.ReactiveJooqx.newInstance((io.zero88.jooqx.ReactiveJooqx) arg,
                                                                            __typeArg_0));
                } catch (Exception e) {
                    return io.vertx.core.Future.failedFuture(e);
                }
                return io.vertx.reactivex.SingleHelper.toFuture(ret, obj -> obj);
            }
        }, handler);
    }

    public <X> void run(
        io.reactivex.functions.Function<io.zero88.jooqx.reactivex.ReactiveJooqx<S>, io.reactivex.Single<X>> function) {
        run(function, ar -> { });
    }

    public <X> io.reactivex.Single<X> rxRun(
        io.reactivex.functions.Function<io.zero88.jooqx.reactivex.ReactiveJooqx<S>, io.reactivex.Single<X>> function) {
        return AsyncResultSingle.toSingle($handler -> {
            run(function, $handler);
        });
    }

    public static <S extends io.vertx.sqlclient.SqlClient> ReactiveJooqx<S> newInstance(io.zero88.jooqx.ReactiveJooqx<S> arg) {
        return arg != null ? new ReactiveJooqx<S>(arg) : null;
    }

    public static <S extends io.vertx.sqlclient.SqlClient> ReactiveJooqx<S> newInstance(io.zero88.jooqx.ReactiveJooqx arg, TypeArg<S> __typeArg_S) {
        return arg != null ? new ReactiveJooqx<S>(arg, __typeArg_S) : null;
    }

}
