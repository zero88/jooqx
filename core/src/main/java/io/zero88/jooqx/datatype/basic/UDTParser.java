package io.zero88.jooqx.datatype.basic;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.Nullable;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Vert.x SQL client treats UDT type in string then it is a parser in common case
 *
 * @implNote Not sure it is standard, should use jOOQ Query
 */
@Experimental
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UDTParser {

    public static @Nullable String[] parse(String udtObject) {
        return parse(udtObject, true);
    }

    public static String[] parse(String udtObject, boolean strict) {
        if (udtObject == null || udtObject.equals("")) {
            return null;
        }
        final int length = udtObject.length();
        if (udtObject.charAt(0) != '(' && udtObject.charAt(length - 1) != ')') {
            if (!strict) {
                return new String[] {udtObject};
            }
            throw new IllegalArgumentException("Invalid udt object in string format");
        }
        final List<String> output = new ArrayList<>();
        boolean next = true;
        boolean beginQuote = false;
        boolean endQuote = false;
        StringBuilder current = new StringBuilder();
        for (int i = 1; i < length - 1; i++) {
            final char c = udtObject.charAt(i);
            if (c == ',') {
                if (next) {
                    output.add(current.toString());
                    continue;
                }
                if (!beginQuote || endQuote) {
                    if (beginQuote) {
                        current.deleteCharAt(0).deleteCharAt(current.length() - 1);
                    }
                    output.add(current.toString());
                    current = new StringBuilder();
                    next = true;
                    beginQuote = false;
                    endQuote = false;
                    continue;
                }
            }
            next = false;
            if (c == '"') {
                char n = i + 1 < length - 1 ? udtObject.charAt(i + 1) : '\0';
                if (n == '"') {
                    continue;
                }
                if (beginQuote && (i == length - 2 || n == ',')) {
                    endQuote = true;
                }
                if (i == 1 || udtObject.charAt(i - 1) == ',') {
                    beginQuote = true;
                }
            }
            current.append(c);
        }
        if (beginQuote && endQuote) {
            current.deleteCharAt(0).deleteCharAt(current.length() - 1);
        }
        output.add(current.toString());
        return output.toArray(new String[] {});
    }

}
