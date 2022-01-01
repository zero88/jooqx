plugins {
    `java-test-fixtures`
}

dependencies {
    api(LogLibs.slf4j)
    api(VertxLibs.core)
    api(DatabaseLibs.jooq)
//    api(ZeroLibs.rql_jooq)
    compileOnly(VertxLibs.jdbc)

    compileOnly(VertxLibs.rx2)
    compileOnly(VertxLibs.codegen)
    annotationProcessor(VertxLibs.codegen)

    testImplementation(VertxLibs.sqlClient)

    testFixturesApi(project(":spi"))
    testFixturesApi(LogLibs.logback)
    testFixturesApi(TestLibs.junit5Api)
    testFixturesApi(TestLibs.junit5Engine)
    testFixturesApi(TestLibs.junit5Params)
    testFixturesApi(TestContainers.junit5)
    testFixturesApi(VertxLibs.junit5)
    testFixturesApi(ZeroLibs.utils)

    testFixturesCompileOnly(UtilLibs.lombok)
    testFixturesAnnotationProcessor(UtilLibs.lombok)
    testFixturesCompileOnly(VertxLibs.codegen)
    testFixturesAnnotationProcessor(VertxLibs.codegen)
    testFixturesCompileOnly(UtilLibs.jetbrainsAnnotations)

    testFixturesImplementation(VertxLibs.rx2)

    testFixturesImplementation(VertxLibs.jdbc)
    testFixturesImplementation(DatabaseLibs.h2)

    testFixturesImplementation(VertxLibs.pgsql)
    testFixturesImplementation(DatabaseLibs.pgsql)
    testFixturesImplementation(TestContainers.pgsql)

    testFixturesImplementation(VertxLibs.mysql)
    testFixturesImplementation(DatabaseLibs.mysql)
    testFixturesImplementation(TestContainers.mysql)

    testFixturesImplementation(DatabaseLibs.agroalApi)
    testFixturesImplementation(DatabaseLibs.hikari)
}

tasks {
    register<JavaCompile>("genSrcCode") {
        genCodeByAnnotation(this, sourceSets)
    }

    compileJava {
        dependsOn(named("genSrcCode"))
    }

    register<JavaCompile>("genTestFixturesCode") {
        genCodeByAnnotation(this, sourceSets, "testFixtures")
    }

    compileTestFixturesJava {
        dependsOn(named("genTestFixturesCode"))
    }
}
