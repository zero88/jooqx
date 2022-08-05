package io.github.zero88.jooqx.adapter;

import org.jooq.Field;

/**
 * Represents for a field wrapper to know the column number of each field
 *
 * @since 2.0.0
 */
public interface FieldWrapper {

    /**
     * @return field
     */
    Field field();

    /**
     * @return the field's column number
     */
    int colNo();

    static FieldWrapper create(Field<?> field, int colNo) {
        if (field == null) {
            return null;
        }
        return new FieldWrapper() {
            @Override
            public Field<?> field() { return field; }

            @Override
            public int colNo() { return colNo; }
        };
    }

}
