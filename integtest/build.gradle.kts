import nu.studer.gradle.jooq.JooqGenerate


subprojects {
    apply(plugin = PluginLibs.jooq)

    configurations.all {
        resolutionStrategy {
            preferProjectModules()
            force(VertxLibs.core)
            force(VertxLibs.sqlClient)
            force(VertxLibs.jdbc)
        }
    }

    dependencies {
        implementation(JooqLibs.jooqMetaExt) // For generate model

        testImplementation(testFixtures(project(":jooqx")))

        testImplementation(DatabaseLibs.agroalApi)
        testImplementation(DatabaseLibs.agroalPool)
        testImplementation(DatabaseLibs.hikari)
        testImplementation(JooqLibs.jooqMeta)

        testImplementation(VertxLibs.rx2)
        testImplementation(VertxLibs.rx3)

        testImplementation(MutinyLibs.core)
        testImplementation(MutinyLibs.jdbc)

        testImplementation(LogLibs.logback)
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
