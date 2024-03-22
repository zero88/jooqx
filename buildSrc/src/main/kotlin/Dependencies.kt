private fun Map<Int, List<Int>>.ver(minor: Int, patch: Int):String = "${minor}.${this[minor]?.get(patch)}"

object UtilLibs {

    object Version {

        const val jetbrainsAnnotations = "24.1.0"
    }

    const val jetbrainsAnnotations = "org.jetbrains:annotations:${Version.jetbrainsAnnotations}"
}

object PluginLibs {

    object Version {

        const val jooq = "5.2"
        const val nexusPublish = "1.1.0"
    }

    const val jooq = "nu.studer.jooq"
}

object JacksonLibs {

    object Version {

        const val jackson = "2.12.0"
    }

    const val annotations = "com.fasterxml.jackson.core:jackson-annotations:${Version.jackson}"
    const val databind = "com.fasterxml.jackson.core:jackson-databind:${Version.jackson}"
    const val datetime = "com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${Version.jackson}"
}

object TestLibs {

    object Version {

        const val junit5 = "5.9.1"
    }

    const val junit5Api = "org.junit.jupiter:junit-jupiter-api:${Version.junit5}"
    const val junit5Engine = "org.junit.jupiter:junit-jupiter-engine:${Version.junit5}"
    const val junit5Vintage = "org.junit.vintage:junit-vintage-engine:${Version.junit5}"
    const val junit5Params = "org.junit.jupiter:junit-jupiter-params:${Version.junit5}"

}

object TestContainers {
    object Version {

        const val ver = "1.19.7"
    }

    const val junit5 = "org.testcontainers:junit-jupiter:${Version.ver}"
    const val pgsql = "org.testcontainers:postgresql:${Version.ver}"
    const val mysql = "org.testcontainers:mysql:${Version.ver}"
}

object VertxLibs {

    object Version {

        private val pool = mapOf(2 to (0..7).toList(), 3 to (0..6).toList(), 4 to (0..0).toList())
        @JvmField val vertxCore = "4.${pool.ver(3, 5)}"
        @JvmField val vertxSQL = "4.${pool.ver(3, 5)}"
        const val vertxJunit = "4.2.5"
    }

    @JvmField val core = "io.vertx:vertx-core:${Version.vertxCore}"
    @JvmField val codegen = "io.vertx:vertx-codegen:${Version.vertxCore}"
    @JvmField val rx2 = "io.vertx:vertx-rx-java2:${Version.vertxCore}"
    @JvmField val rx3 = "io.vertx:vertx-rx-java3:${Version.vertxCore}"
    @JvmField val junit5 = "io.vertx:vertx-junit5:${Version.vertxJunit}"

    @JvmField val sqlClient = "io.vertx:vertx-sql-client:${Version.vertxSQL}"
    @JvmField val jdbc = "io.vertx:vertx-jdbc-client:${Version.vertxSQL}"
    @JvmField val pgsql = "io.vertx:vertx-pg-client:${Version.vertxSQL}"
    @JvmField val mysql = "io.vertx:vertx-mysql-client:${Version.vertxSQL}"
    @JvmField val db2 = "io.vertx:vertx-db2-client:${Version.vertxSQL}"
    @JvmField val mssql = "io.vertx:vertx-mssql-client:${Version.vertxSQL}"
}

object MutinyLibs {
    object Version {

        const val mutiny = "2.27.0"
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

object LogLibs {

    object Version {

        const val slf4j = "1.7.36"
        const val logback = "1.5.3"
        const val log4j2 = "2.23.1"
    }

    const val slf4j = "org.slf4j:slf4j-api:${Version.slf4j}"
    const val logback = "ch.qos.logback:logback-classic:${Version.logback}"
    const val slf4jSimple = "org.slf4j:slf4j-simple:${Version.slf4j}"
    const val log4j2Api = "org.apache.logging.log4j:log4j-api:${Version.log4j2}"
    const val log4j2Core = "org.apache.logging.log4j:log4j-core:${Version.log4j2}"
    const val log4j2slf4j = "org.apache.logging.log4j:log4j-slf4j-impl:${Version.log4j2}"
}

object JooqLibs {
    object Version {

        private val pool = mapOf(14 to (0..13).toList(), 15 to (0..10).toList(), 16 to (0..6).toList())
        @JvmField val jooq = "3.${pool.ver(14 ,13)}"
    }

    @JvmField val jooq = "org.jooq:jooq:${Version.jooq}"
    @JvmField val jooqMeta = "org.jooq:jooq-meta:${Version.jooq}"
    @JvmField val jooqMetaExt = "org.jooq:jooq-meta-extensions:${Version.jooq}"
    @JvmField val jooqCodegen = "org.jooq:jooq-codegen:${Version.jooq}"
}

object DatabaseLibs {

    object Version {

        const val h2 = "1.4.200"
        const val pgsql = "42.2.23"
        const val mysql = "8.0.23"
        const val jpa = "2.2"
        const val jta = "1.3"
        const val sqlite = "3.36.0.1"
        const val hsqldb = "2.5.2"
        const val derby = "10.14.2.0"
        const val agroal = "1.9"
        const val c3p0 = "0.9.5.4"
        const val hikari = "4.0.2"
    }

    const val agroalApi = "io.agroal:agroal-api:${Version.agroal}"
    const val agroalPool = "io.agroal:agroal-pool:${Version.agroal}"
    const val c3p0 = "com.mchange:c3p0:${Version.c3p0}"
    const val hikari = "com.zaxxer:HikariCP:${Version.hikari}"
    const val h2 = "com.h2database:h2:${Version.h2}"
    const val pgsql = "org.postgresql:postgresql:${Version.pgsql}"
    const val mysql = "mysql:mysql-connector-java:${Version.mysql}"
    const val sqlite = "org.xerial:sqlite-jdbc:${Version.sqlite}"
    const val derby = "org.apache.derby:derby:${Version.derby}"
    const val hsqldb = "org.hsqldb:hsqldb:${Version.hsqldb}"
}

object ZeroLibs {
    object Version {

        const val utils = "2.0.0"
        const val rsql = "2.2.1"
    }

    const val rsql = "io.github.zero88:rsql-parser:${Version.rsql}"
    const val utils = "io.github.zero88:java-utils:${Version.utils}"

}

object PlayioPlugin {
    object Version {

        const val gradlePlugin = "0.3.0"
    }

    const val oss = "cloud.playio.gradle.oss"
    const val root = "cloud.playio.gradle.root"
    const val antora = "cloud.playio.gradle.antora"
    const val pandoc = "cloud.playio.gradle.pandoc"
    const val docgen = "cloud.playio.gradle.docgen"
    const val codegen = "cloud.playio.gradle.codegen"

}
