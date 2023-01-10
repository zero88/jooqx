package io.github.zero88.jooqx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jooq.Field;
import org.jooq.InsertSetStep;
import org.jooq.MergeMatchedSetStep;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.UpdateSetStep;

/**
 * Represents a holder keeping dummy values and list of binding records
 *
 * @apiNote With {@code dummy value records}, you can predefine a field value, then if a bind record is missing this
 *     field value then system will auto-detect and fallback to the predefined value. See: {@link #registerValue(Field,
 *     Object)}
 * @see <a href="https://www.jooq.org/doc/latest/manual/sql-execution/batch-execution/">JDBC batch operations</a>
 * @see InsertSetStep#set(Map)
 * @see UpdateSetStep#set(Map)
 * @see MergeMatchedSetStep#set(Map)
 * @since 1.0.0
 */
public final class BindBatchValues {

    private final Map<Object, Object> dummyValues = new LinkedHashMap<>();
    private final List<Record> records = new ArrayList<>();

    public BindBatchValues register(String... fields) {
        Arrays.stream(fields).filter(Objects::nonNull).forEach(f -> this.dummyValues.put(f, null));
        return this;
    }

    public BindBatchValues register(Field<?>... fields) {
        Arrays.stream(fields).filter(Objects::nonNull).forEach(f -> this.dummyValues.put(f, null));
        return this;
    }

    public BindBatchValues register(Name... fields) {
        Arrays.stream(fields).filter(Objects::nonNull).forEach(f -> this.dummyValues.put(f, null));
        return this;
    }

    public <T> BindBatchValues registerValue(@NotNull Field<T> field, Object value) {
        this.dummyValues.put(field, value);
        return this;
    }

    public BindBatchValues add(Record... recs) {
        return this.add(Arrays.asList(recs));
    }

    public BindBatchValues add(Collection<Record> recs) {
        recs.stream().filter(Objects::nonNull).forEachOrdered(records::add);
        return this;
    }

    @NotNull
    public List<String> getMappingFields() {
        return dummyValues.keySet().stream().map(this::checkAndReturnField).collect(Collectors.toList());
    }

    @NotNull
    public List<Object> getMappingValues() {
        return new ArrayList<>(dummyValues.values());
    }

    @NotNull
    public Map<?, ?> getDummyValues() {
        final LinkedHashMap<Object, Object> map = new LinkedHashMap<>();
        this.dummyValues.forEach((k, v) -> map.put(k, null));
        return map;
    }

    @NotNull
    public List<Record> getRecords() {
        return Collections.unmodifiableList(this.records);
    }

    /**
     * Batch record size
     *
     * @return total record
     */
    public int size() {
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
