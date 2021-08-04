object UtilLibs {

    object Version {

        const val lombok = "1.18.16"
        const val jetbrainsAnnotations = "20.1.0"
    }

    const val lombok = "org.projectlombok:lombok:${Version.lombok}"
    const val jetbrainsAnnotations = "org.jetbrains:annotations:${Version.jetbrainsAnnotations}"
}

object PluginLibs {

    object Version {

        const val nexusStaging = "0.22.0"
        const val jooq = "5.2"
    }

    const val nexusStaging = "io.codearte.nexus-staging"
    const val jooq = "nu.studer.jooq"
}

object TestLibs {

    object Version {

        const val junit5 = "5.7.0"
    }

    const val junit5Api = "org.junit.jupiter:junit-jupiter-api:${Version.junit5}"
    const val junit5Engine = "org.junit.jupiter:junit-jupiter-engine:${Version.junit5}"
    const val junit5Vintage = "org.junit.vintage:junit-vintage-engine:${Version.junit5}"
    const val junit5Params = "org.junit.jupiter:junit-jupiter-params:${Version.junit5}"

}

object TestContainers {
    object Version {

        const val ver = "1.15.2"
    }

    const val junit5 = "org.testcontainers:junit-jupiter:${Version.ver}"
    const val pgsql = "org.testcontainers:postgresql:${Version.ver}"
    const val mysql = "org.testcontainers:mysql:${Version.ver}"
}

object VertxLibs {

    object Version {

        const val vertx = "4.1.2"
        const val docgen = "0.9.3"
//        const val vertxSQL = "4.2.0-SNAPSHOT"
        const val vertxSQL = "4.1.2"
    }

    const val core = "io.vertx:vertx-core:${Version.vertx}"
    const val junit5 = "io.vertx:vertx-junit5:${Version.vertx}"
    const val sqlClient = "io.vertx:vertx-sql-client:${Version.vertxSQL}"
    const val jdbc = "io.vertx:vertx-jdbc-client:${Version.vertxSQL}"
    const val pgsql = "io.vertx:vertx-pg-client:${Version.vertx}"
    const val mysql = "io.vertx:vertx-mysql-client:${Version.vertx}"
    const val db2 = "io.vertx:vertx-db2-client:${Version.vertx}"
    const val mssql = "io.vertx:vertx-mssql-client:${Version.vertx}"
    const val codegen = "io.vertx:vertx-codegen:${Version.vertx}"
    const val docgen = "io.vertx:vertx-docgen:${Version.docgen}"
    const val rx2 = "io.vertx:vertx-rx-java2:${Version.vertx}"

}

object LogLibs {

    object Version {

        const val slf4j = "1.7.30"
        const val logback = "1.2.3"
    }

    const val slf4j = "org.slf4j:slf4j-api:${Version.slf4j}"
    const val logback = "ch.qos.logback:logback-classic:${Version.logback}"
}

object DatabaseLibs {

    object Version {

        const val jooq = "3.14.8"
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
    const val jooq = "org.jooq:jooq:${Version.jooq}"
    const val jooqMeta = "org.jooq:jooq-meta:${Version.jooq}"
    const val jooqMetaExt = "org.jooq:jooq-meta-extensions:${Version.jooq}"
    const val jooqCodegen = "org.jooq:jooq-codegen:${Version.jooq}"
    const val sqlite = "org.xerial:sqlite-jdbc:${Version.sqlite}"
    const val derby = "org.apache.derby:derby:${Version.derby}"
    const val hsqldb = "org.hsqldb:hsqldb:${Version.hsqldb}"
}

object ZeroLibs {
    object Version {

        const val utils = "1.0.1"
        const val rSql = "0.9.0"
        const val plugin = "1.0.0"
    }

    const val rql_jooq = "io.github.zero88:rql-jooq:${Version.rSql}"
    const val utils = "io.github.zero88:java-utils:${Version.utils}"

    object Plugins {

        const val oss = "io.github.zero88.qwe.gradle.oss"
        const val root = "io.github.zero88.qwe.gradle.root"
    }
}
