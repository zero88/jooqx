import org.gradle.api.Project

private fun Map<Int, List<Int>>.ver(minor: Int, patch: Int): String = "${minor}.${this[minor]?.get(patch)}"

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
