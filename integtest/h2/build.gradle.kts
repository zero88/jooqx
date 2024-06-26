import nu.studer.gradle.jooq.JooqGenerate
import org.jooq.meta.jaxb.Logging
import org.jooq.meta.jaxb.Property

dependencies {
    jooqGenerator(libs.h2Jdbc)
    jooqGenerator(libs.jooqMetaExt)
    jooqGenerator(libs.bundles.slf4jImpl)
}

jooq {
    version.set(if (JavaVersion.current().majorVersion == "8") libs.versions.jooq.jdk8.get() else libs.versions.jooq.jdk17.get())

    configurations {
        create("testH2Schema") {
            jooqConfiguration.apply {
                logging = Logging.INFO
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                    database.apply {
                        name = "org.jooq.meta.extensions.ddl.DDLDatabase"
                        properties.add(
                            Property().withKey("scripts").withValue("src/main/resources/h2_schema.sql")
                        )
                    }
                    generate.apply {
                        isRecords = true
                        isFluentSetters = true
                        isDeprecated = false
                        isImmutablePojos = false
                        isInterfaces = false
                        isDaos = false
                    }
                    target.apply {
                        packageName = "io.github.zero88.sample.model.h2"
                        directory = "build/generated/h2"
                    }
                }
            }
        }
    }
}

sourceSets {
    main {
        java.srcDirs(tasks.withType<JooqGenerate>().map { it.outputDir.get().asFile })
    }
}
