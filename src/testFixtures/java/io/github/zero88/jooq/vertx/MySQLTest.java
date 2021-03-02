package io.github.zero88.jooq.vertx;

import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

public interface MySQLTest {

    DBContainerProvider<MySQLContainer<?>> MYSQL = new DBContainerProvider<MySQLContainer<?>>() {

        @Override
        public MySQLContainer<?> get() {
            return get("mysql:8.0");
        }

        @Override
        public MySQLContainer<?> get(String imageName) {
            return new MySQLContainer<>(DockerImageName.parse(imageName)).withDatabaseName("foo")
                                                                         .withUsername("foo")
                                                                         .withPassword("secret");
        }
    };

}
