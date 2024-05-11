import org.gradle.api.Project

private fun Map<Int, List<Int>>.ver(minor: Int, patch: Int): String = "${minor}.${this[minor]?.get(patch)}"

object MutinyLibs {
    object Version {

        const val mutiny = "2.30.1"
    }

    const val core = "io.smallrye.reactive:smallrye-mutiny-vertx-core:${Version.mutiny}"
    const val sqlClient = "io.smallrye.reactive:smallrye-mutiny-vertx-sql-client:${Version.mutiny}"
    const val jdbc = "io.smallrye.reactive:smallrye-mutiny-vertx-jdbc-client:${Version.mutiny}"
    const val pgsql = "io.smallrye.reactive:smallrye-mutiny-vertx-pg-client:${Version.mutiny}"
    const val mysql = "io.smallrye.reactive:smallrye-mutiny-vertx-mysql-client:${Version.mutiny}"
    const val db2 = "io.smallrye.reactive:smallrye-mutiny-vertx-db2-client:${Version.mutiny}"
    const val oracle = "io.smallrye.reactive:smallrye-mutiny-vertx-oracle-client:${Version.mutiny}"
    const val mssql = "io.smallrye.reactive:smallrye-mutiny-vertx-mssql-client:${Version.mutiny}"
    const val codegen = "io.smallrye.reactive:vertx-mutiny-generator:${Version.mutiny}"
}

object JooqLibs {
    object Version {

        private val pool = mapOf(
            14 to (0..16).toList(),
            15 to (0..12).toList(),
            16 to (0..23).toList(),
            17 to (0..22).toList(),
            18 to (0..13).toList(),
            19 to (0..6).toList(),
        )
        @JvmField val jooq = "3.${pool.ver(14, 16)}"
    }

    @JvmField val jooq = "org.jooq:jooq:${Version.jooq}"
    @JvmField val jooqMeta = "org.jooq:jooq-meta:${Version.jooq}"
    @JvmField val jooqMetaExt = "org.jooq:jooq-meta-extensions:${Version.jooq}"
}

object DatabaseContainer {

    data class Container(val defaultImage: String, val jdbcPrefix: String, val supportedVersions: List<String>) {}

    enum class Containers(val container: Container) {
        // https://endoflife.date/postgresql
        postgres(Container("postgres:16-alpine", "postgresql", listOf("16-alpine", "14-alpine", "12-alpine"))),

        // https://endoflife.date/mysql
        mysql(Container("mysql:8.3", "mysql", listOf("8.3", "8.0")))
    }

    fun findImage(project: Project): String {
        val container = Containers.valueOf(project.name).container
        val jdbcDbImage: String = when (val version = project.findProperty("dbVersion")) {
            in container.supportedVersions -> container.defaultImage.replaceAfter(":", "$version")
            else                           -> container.defaultImage
        }
        return jdbcDbImage.replaceBefore(":", container.jdbcPrefix)
    }
}

object ZeroLibs {
    object Version {

        const val utils = "2.0.0"
        const val rsql = "2.2.1"
    }

    const val rsql = "io.github.zero88:rsql-parser:${Version.rsql}"
    const val utils = "io.github.zero88:java-utils:${Version.utils}"

}
