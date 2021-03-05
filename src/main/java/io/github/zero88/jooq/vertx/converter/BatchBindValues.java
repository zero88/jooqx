package io.github.zero88.jooq.vertx.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jooq.Field;
import org.jooq.InsertSetStep;
import org.jooq.MergeMatchedSetStep;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.UpdateSetStep;

import lombok.NonNull;

/**
 * @see <a href="https://www.jooq.org/doc/latest/manual/sql-execution/batch-execution/">JDBC batch operations</a>
 * @see InsertSetStep#set(Map)
 * @see UpdateSetStep#set(Map)
 * @see MergeMatchedSetStep#set(Map)
 */
public final class BatchBindValues {

    private final Map<Object, Object> dummyValues = new LinkedHashMap<>();
    private final List<Record> records = new ArrayList<>();

    public BatchBindValues register(@NonNull String field) {
        this.dummyValues.put(field, null);
        return this;
    }

    public <T> BatchBindValues register(@NonNull Field<T> field) {
        this.dummyValues.put(field, null);
        return this;
    }

    public BatchBindValues register(@NonNull Name field) {
        this.dummyValues.put(field, null);
        return this;
    }

    public <T> BatchBindValues register(@NonNull Field<T> field, Object value) {
        this.dummyValues.put(field, value);
        return this;
    }

    public BatchBindValues add(Record... recs) {
        return this.add(Arrays.asList(recs));
    }

    public BatchBindValues add(Collection<Record> recs) {
        recs.stream().filter(Objects::nonNull).forEachOrdered(records::add);
        return this;
    }

    @NonNull
    public List<String> getMappingFields() {
        return dummyValues.keySet().stream().map(this::checkAndReturnField).collect(Collectors.toList());
    }

    @NonNull
    public List<Object> getMappingValues() {
        return new ArrayList<>(dummyValues.values());
    }

    @NonNull
    public Map<?, ?> getDummyValues() {
        final LinkedHashMap<Object, Object> map = new LinkedHashMap<>();
        this.dummyValues.forEach((k, v) -> map.put(k, null));
        return map;
    }

    @NonNull
    public List<Record> getRecords() {
        return Collections.unmodifiableList(this.records);
    }

    public int batchSize() {
        return this.records.size();
    }

    private String checkAndReturnField(Object field) {
        if (field instanceof String) {
            return (String) field;
        }
        if (field instanceof Field) {
            return ((Field<?>) field).getName();
        }
        return field.toString();
    }

}
