package io.github.zero88.jooq.vertx;

import org.jooq.Configuration;
import org.jooq.Param;
import org.jooq.Query;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;

import io.github.zero88.jooq.vertx.converter.ext.PgConverter;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.impl.ArrayTuple;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueryHelper {

    public static final String PATTERN = "(?<!:):(?!:)";

    public String toPreparedQuery(@NonNull Configuration configuration, @NonNull Query query) {
        return toPreparedQuery(configuration, query, null);
    }

    public String toPreparedQuery(@NonNull Configuration configuration, @NonNull Query query, ParamType paramType) {
        if (!query.isExecutable()) {
            throw new IllegalArgumentException("Query is not executable: " + query.getSQL());
        }
        if (log.isTraceEnabled()) {
            log.debug("DEFAULT:             {}", query.getSQL());
            log.debug("NAMED:               {}", query.getSQL(ParamType.NAMED));
            log.debug("INLINED:             {}", query.getSQL(ParamType.INLINED));
            log.debug("NAMED_OR_INLINED:    {}", query.getSQL(ParamType.NAMED_OR_INLINED));
            log.debug("INDEXED:             {}", query.getSQL(ParamType.INDEXED));
            log.debug("FORCE_INDEXED:       {}", query.getSQL(ParamType.FORCE_INDEXED));
        }
        if (SQLDialect.POSTGRES.supports(configuration.dialect()) && paramType == null) {
            final String sql = query.getSQL(ParamType.NAMED).replaceAll(PATTERN, "\\$");
            if (log.isDebugEnabled()) {
                log.debug("POSTGRESQL:          {}", sql);
            }
            return sql;
        }
        final String sql = query.getSQL(paramType == null ? ParamType.INDEXED : paramType);
        if (log.isDebugEnabled()) {
            log.debug("Prepare Query:          {}", sql);
        }
        return sql;
    }

    public JsonArray toBindValues(@NonNull Query query) {
        JsonArray array = new JsonArray();
        for (Param<?> param : query.getParams().values()) {
            if (!param.isInline()) {
                array.add(convertToDatabaseType(param));
            }
        }
        return array;
    }

    public Tuple getBindValues(@NonNull Query query) {
        ArrayTuple bindValues = new ArrayTuple(query.getParams().size());
        for (Param<?> param : query.getParams().values()) {
            if (!param.isInline()) {
                Object value = convertToDatabaseType(param);
                bindValues.addValue(value);
            }
        }
        return bindValues;
    }

    @SuppressWarnings("rawtypes")
    public <U> Object convertToDatabaseType(Param<U> param) {
        /*
         * https://github.com/reactiverse/reactive-pg-client/issues/191 enum types are treated as unknown
         * DataTypes. Workaround is to convert them to string before adding to the Tuple.
         */
        if (Enum.class.isAssignableFrom(param.getBinding().converter().toType())) {
            if (param.getValue() == null) {
                return null;
            }
            return param.getValue().toString();
        }
        // jooq treats BINARY types as byte[] but the reactive client expects a Buffer to write to blobs
        if (byte[].class.isAssignableFrom(param.getBinding().converter().fromType())) {
            byte[] bytes = (byte[]) param.getBinding().converter().to(param.getValue());
            if (bytes == null) {
                return null;
            }
            return Buffer.buffer(bytes);
        }
        if (param.getBinding().converter() instanceof PgConverter) {
            return ((PgConverter) param.getBinding().converter()).rowConverter().to(param.getValue());
        }
        return param.getBinding().converter().to(param.getValue());
    }

}
