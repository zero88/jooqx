package io.github.zero88.jooq.vertx;

import java.util.function.Supplier;

import org.testcontainers.containers.JdbcDatabaseContainer;

public interface DBContainerProvider<S extends JdbcDatabaseContainer<?>> extends Supplier<S> {

    S get(String imageName);

}
