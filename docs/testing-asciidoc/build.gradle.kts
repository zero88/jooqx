import cloud.playio.gradle.antora.AntoraType
import cloud.playio.gradle.antora.tasks.AntoraCopyTask
import cloud.playio.gradle.generator.docgen.AsciidocGenTask

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.antora)
    alias(libs.plugins.docgen)
}

dependencies {
    compileOnly(projects.spi)
    compileOnly(testFixtures(projects.jooqx))
//    compileOnly(projects.rsql.jooq)
    compileOnly(projects.integtest.postgres)
    compileOnly(libs.jdbcVertx)
    compileOnly(libs.postgresVertx)
    compileOnly(libs.mysqlVertx)

    implementation(libs.jdbcVertx)
    implementation(libs.postgresVertx)
    implementation(libs.mysqlVertx)
}

documentation {
    antora {
        antoraModule.set("testing")
        antoraType.set(AntoraType.MODULE)
        javadocInDir.from(project(":jooqx").tasks.named<Javadoc>("testFixturesJavadoc"))
    }
}

tasks {
    named<AntoraCopyTask>("antoraPartials") {
        from(withType<AsciidocGenTask>())
        include("*.adoc")
    }
}
