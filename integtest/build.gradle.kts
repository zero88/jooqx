import nu.studer.gradle.jooq.JooqGenerate

allprojects {
    configurations.all {
        resolutionStrategy {
            preferProjectModules()
            force(VertxLibs.core)
            force(VertxLibs.sqlClient)
            force(VertxLibs.jdbc)
        }
    }

    tasks {
        withType<JooqGenerate> { allInputsDeclared.set(true) }
        register("generateJooq") {
            group = "jooq"
            dependsOn(withType<JooqGenerate>())
        }
        test {
            project.findProperty("dbImage")?.let { systemProperty("dbImage", it) }
        }
    }

}

dependencies {
    testImplementation(project(":sample:model"))
    testImplementation(testFixtures(project(":jooqx")))
    testImplementation(project(":rsql:jooq"))
    testImplementation(VertxLibs.sqlClient)
    testImplementation(VertxLibs.jdbc)

    testImplementation(DatabaseLibs.agroalApi)
    testImplementation(DatabaseLibs.agroalPool)
    testImplementation(DatabaseLibs.hikari)
    testImplementation(DatabaseLibs.jooqMeta)

    testImplementation(DatabaseLibs.h2)

    testImplementation(DatabaseLibs.pgsql)
    testImplementation(VertxLibs.pgsql)
    testImplementation(TestContainers.pgsql)

    testImplementation(DatabaseLibs.mysql)
    testImplementation(VertxLibs.mysql)
    testImplementation(TestContainers.mysql)

    testImplementation(VertxLibs.rx2)
    testImplementation(VertxLibs.rx3)

    testImplementation(LogLibs.logback)

    testImplementation(MutinyLibs.core)
    testImplementation(MutinyLibs.jdbc)
}
