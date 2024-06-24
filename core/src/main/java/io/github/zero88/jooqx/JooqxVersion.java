package io.github.zero88.jooqx;

import static io.github.zero88.jooqx.Utils.brackets;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jooq.Constants;

import io.vertx.core.impl.launcher.commands.VersionCommand;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;

/**
 * JOOQ.X version
 *
 * @since 2.0.0
 */
public final class JooqxVersion {

    private static final Logger LOGGER = LoggerFactory.getLogger(JooqxVersion.class);
    private static String version;
    private static String jooqVersion;

    private JooqxVersion() { }

    /**
     * @return {@code jooq.x} version
     */
    @Internal
    public static String getVersion() {
        if (version == null) {
            version = loadFromFile();
        }
        return version;
    }

    /**
     * Query version from the direct dependent libraries: {@code jOOQ}, {@code vert.x}, and combine with {@code jooq.x}
     * version
     *
     * @return versions in JSON format
     * @see #getVersion()
     */
    public static JsonObject versionComponents() {
        return JsonObject.of("jOOQ", getJooqVersion(), "Vert.x", VersionCommand.getVersion(), "jooq.x", getVersion());
    }

    /**
     * Validate the version compatibility between {@code jooq.x} and the direct dependent libraries
     */
    public static void validate() {
        try {
            validateJooq(getVersion(), getJooqVersion());
        } catch (IncompatibleVersion ex) {
            LOGGER.warn(ex.getMessage());
        } catch (Exception ex) {
            LOGGER.trace("Validate compatibility version failed", ex);
        }
    }

    static void validateJooq(String jooqxVer, String jooqVersion) {
        boolean isJvm8 = jooqxVer.contains("+jvm8");
        String[] versions = jooqVersion.split("\\.", 3);
        boolean isMajor3 = Objects.equals(versions[0], "3");
        int jooqMinor = Integer.parseInt(versions[1]);
        if (isMajor3 && isJvm8 == (jooqMinor >= 18)) {
            throw new IncompatibleVersion(
                "jOOQ.x version" + brackets(jooqxVer) + " is not compatible with jOOQ version" + brackets(jooqVersion) +
                ". Please refer the documentation: " + docUrl(jooqxVer) + " for more details.");
        }
    }

    private static String docUrl(String jooqxVer) {
        String releaseVer = jooqxVer.replace("+jvm8", "");
        releaseVer = releaseVer.contains("-SNAPSHOT") ? "main" : releaseVer;
        return "https://zero88.github.io/webdocs/jooqx/" + releaseVer + "/core-usage.html#_compatibility_matrix";
    }

    private static String getJooqVersion() {
        if (jooqVersion == null) {
            try {
                final Class<?> aClass = JooqxVersion.class.getClassLoader().loadClass("org.jooq.Constants");
                final Field versionField = aClass.getDeclaredField("VERSION");
                jooqVersion = (String) versionField.get(null);
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException ex) {
                LOGGER.debug("Unable to load runtime jOOQ version, fallback to build version", ex);
                jooqVersion = Constants.VERSION;
            }
        }
        return jooqVersion;
    }

    private static String loadFromFile() {
        // in build process, version will be auto-inject to manifest by
        // `JooqxVersion.class.getPackage().getImplementationVersion()`
        // however, fat-jar will override the package version by itself application version
        // so load from file is safe choice
        LOGGER.debug("Loading `jooq.x` version from file `jooqx-version.txt`...");
        try (InputStream is = JooqxVersion.class.getClassLoader().getResourceAsStream("jooqx-version.txt")) {
            if (is == null) {
                LOGGER.warn("Cannot find jooqx-version.txt on classpath");
                return null;
            }
            try (Scanner scanner = new Scanner(is, StandardCharsets.UTF_8.name()).useDelimiter("\\A")) {
                return scanner.hasNext() ? scanner.next().trim() : "";
            }
        } catch (IOException ex) {
            LOGGER.warn("Cannot read jooqx-version.txt on classpath", ex);
            return null;
        }
    }

    static class IncompatibleVersion extends RuntimeException {

        public IncompatibleVersion(String msg) { super(msg); }

    }

}
