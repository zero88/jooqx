package io.github.zero88.jooqx.spi.mysql;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Objects;

import io.github.zero88.jooqx.ScriptRunner;
import io.github.zero88.utils.Strings;

public class MySQLInitializer {

    public static void init(Connection connection) {
        runScript(connection, Strings.requireNotBlank(System.getProperty("dbSchemaFile"), "Required DB file"));
    }

    public static void runScript(Connection connection, String... scriptFiles) {
        final ScriptRunner scriptRunner = new ScriptRunner(connection).setStopOnError(true).setSendFullScript(true);
        final ClassLoader cl = MySQLInitializer.class.getClassLoader();
        Arrays.stream(scriptFiles).filter(Strings::isNotBlank).map(Paths::get).map(p -> {
            try {
                return p.isAbsolute() && Files.exists(p)
                       ? Files.newInputStream(p)
                       : cl.getResourceAsStream(p.toString());
            } catch (IOException e) {
                System.err.println("WARN Unable read SQL file [" + p + "]. Error: " + e);
            }
            return null;
        }).filter(Objects::nonNull).forEach(is -> {
            try (Reader reader = new InputStreamReader(is)) {
                scriptRunner.runScript(reader);
            } catch (IOException e) {
                System.out.println("WARN Unable close stream");
            }
        });
    }

}
