import cloud.playio.gradle.antora.AntoraType
import cloud.playio.gradle.antora.tasks.AntoraCopyTask
import cloud.playio.gradle.generator.docgen.AsciidocGenTask

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.antora)
    alias(libs.plugins.docgen)
}

dependencies {
    compileOnly(project(":spi"))
    compileOnly(testFixtures(project(":jooqx")))
//    compileOnly(project(":rsql:jooq"))
    compileOnly(project(":integtest:postgres"))
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
