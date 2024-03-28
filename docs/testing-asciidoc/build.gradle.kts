import cloud.playio.gradle.antora.AntoraType
import cloud.playio.gradle.antora.tasks.AntoraCopyTask
import cloud.playio.gradle.generator.docgen.AsciidocGenTask

plugins {
    id(PlayioPlugin.antora)
    id(PlayioPlugin.docgen)
}

dependencies {
    compileOnly(project(":spi"))
    compileOnly(testFixtures(project(":jooqx")))
//    compileOnly(project(":rsql:jooq"))
    compileOnly(project(":integtest:postgres"))
    compileOnly(VertxLibs.jdbc)
    compileOnly(VertxLibs.pgsql)
    compileOnly(VertxLibs.mysql)
    compileOnly(VertxLibs.rx2)

    implementation(VertxLibs.jdbc)
    implementation(VertxLibs.pgsql)
    implementation(VertxLibs.mysql)
    implementation(VertxLibs.rx2)
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
