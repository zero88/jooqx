package io.github.zero88.jooq.vertx;

import java.util.List;

import org.jooq.Configuration;
import org.jooq.Query;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;

import io.github.zero88.jooq.vertx.converter.BindParamConverter;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Query helper wraps a bind param converter and supports some default prepare statement methods
 *
 * @param <T> Type of SQL bind value holder
 * @see BindParamConverter
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public final class QueryHelper<T> {

    public static final String NAMED_PARAM_PATTERN = "(?<!:):(?!:)";

    @NonNull
    private final BindParamConverter<T> paramConverter;

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
            final String sql = query.getSQL(ParamType.NAMED).replaceAll(NAMED_PARAM_PATTERN, "\\$");
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

    public T toBindValues(@NonNull Query query) {
        return paramConverter.convert(query.getParams());
    }

    public List<T> toBindValues(@NonNull Query query, @NonNull BindBatchValues bindBatchValues) {
        return paramConverter.convert(query.getParams(), bindBatchValues);
    }

}
