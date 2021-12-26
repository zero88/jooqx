import org.jooq.meta.jaxb.Property

plugins {
    id(PluginLibs.jooq)
}

dependencies {
    api(project(":rsql:core"))
    api(DatabaseLibs.jooq)

    testImplementation(project(":rsql:core"))
    testImplementation(LogLibs.logback)
    testImplementation(DatabaseLibs.jooqMeta)
    testImplementation(DatabaseLibs.jooqMetaExt)

    jooqGenerator(DatabaseLibs.h2)
    jooqGenerator(DatabaseLibs.jooqMeta)
    jooqGenerator(DatabaseLibs.jooqMetaExt)
}

sourceSets.test {
    java.srcDirs("src/test/java", "build/generated/test/java")
}

jooq {
    edition.set(nu.studer.gradle.jooq.JooqEdition.OSS)
    configurations {
        create("Test") {
            generateSchemaSourceOnCompilation.set(true)
            jooqConfiguration.apply {
                logging = org.jooq.meta.jaxb.Logging.WARN
                jdbc = null
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database.apply {
                        name = "org.jooq.meta.extensions.ddl.DDLDatabase"
                        properties.add(Property().withKey("scripts").withValue("src/test/resources/database.sql"))
                        properties.add(Property().withKey("sort").withValue("semantic"))
                        properties.add(Property().withKey("unqualifiedSchema").withValue("none"))
                    }
                    strategy.apply {
                        name = "org.jooq.codegen.DefaultGeneratorStrategy"
                    }
                    generate.apply {
                        withRelations(true)
                        withDeprecated(false)
                        withRecords(true)
                        withImmutablePojos(true)
                        withFluentSetters(true)
                    }
                    target.apply {
                        packageName = "io.zero88.rsql.jooq"
                        directory = "build/generated/test/java"
                    }
                }
            }
        }
    }
}

tasks.compileTestJava {
    dependsOn(tasks["generateTestJooq"])
}
