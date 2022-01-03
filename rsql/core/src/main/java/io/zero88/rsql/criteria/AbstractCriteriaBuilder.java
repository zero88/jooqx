package io.zero88.rsql.criteria;

import cz.jirutka.rsql.parser.ast.Node;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractCriteriaBuilder<T extends Node> implements CriteriaBuilder<T> {

    protected T node;

    public final @NonNull T node() {
        return node;
    }

}
