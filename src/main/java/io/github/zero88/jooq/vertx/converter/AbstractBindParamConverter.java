package io.github.zero88.jooq.vertx.converter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.jooq.Param;

import io.github.zero88.jooq.vertx.BindBatchValues;

import lombok.NonNull;

public abstract class AbstractBindParamConverter<T> implements BindParamConverter<T> {

    @Override
    public T convert(@NonNull Map<String, Param<?>> params) {
        return doConvert(params, (k, v) -> v.getValue());
    }

    @Override
    public List<T> convert(@NonNull Map<String, Param<?>> params, @NonNull BindBatchValues bindBatchValues) {
        final List<String> fields = bindBatchValues.getMappingFields();
        final List<Object> values = bindBatchValues.getMappingValues();
        return bindBatchValues.getRecords().stream().map(record -> doConvert(params, (paramNameOrIndex, param) -> {
            if (param.getParamName() != null) {
                return record.get(param.getParamName());
            }
            try {
                final int index = Integer.parseInt(paramNameOrIndex) - 1;
                return Optional.ofNullable((Object) record.get(record.field(fields.get(index))))
                               .orElseGet(() -> values.get(index));
            } catch (NumberFormatException e) {
                return record.get(paramNameOrIndex);
            }
        })).collect(Collectors.toList());
    }

    protected abstract T doConvert(Map<String, Param<?>> params, BiFunction<String, Param<?>, ?> queryValue);

}
