plugins {
    id(ZeroLibs.Plugins.oss) version ZeroLibs.Version.plugin
    id(ZeroLibs.Plugins.root) version ZeroLibs.Version.plugin apply false
    id(PluginLibs.jooq) version PluginLibs.Version.jooq apply false
    id(PluginLibs.nexusStaging) version PluginLibs.Version.nexusStaging
    `java-test-fixtures`
    eclipse
    idea
}

apply(plugin = ZeroLibs.Plugins.root)

allprojects {
    apply(plugin = ZeroLibs.Plugins.oss)
    group = "io.github.zero88.jooq"

    repositories {
        mavenLocal()
        maven { url = uri("https://oss.sonatype.org/content/groups/public/") }
        mavenCentral()
        jcenter()
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
    }

    tasks {
        javadoc {
            options {
                this as StandardJavadocDocletOptions
                if (JavaVersion.current().isJava8Compatible) {
                    addBooleanOption("Xdoclint:none", true)
                }
            }
        }
    }

    dependencies {
        compileOnly(UtilLibs.lombok)
        annotationProcessor(UtilLibs.lombok)

        testImplementation(TestLibs.junit5Api)
        testImplementation(TestLibs.junit5Engine)
        testImplementation(TestLibs.junit5Vintage)
        testImplementation(TestLibs.jsonAssert)
        testCompileOnly(UtilLibs.lombok)
        testAnnotationProcessor(UtilLibs.lombok)
    }

    qwe {
        zero88.set(true)
        publishingInfo {
            enabled.set(true)
            homepage.set("https://github.com/zero88/vertx-jooq-dsl")
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://github.com/zero88/vertx-jooq-dsl/blob/master/LICENSE")
            }
            scm {
                connection.set("scm:git:git://git@github.com:zero88/vertx-jooq-dsl.git")
                developerConnection.set("scm:git:ssh://git@github.com:zero88/vertx-jooq-dsl.git")
                url.set("https://github.com/zero88/vertx-jooq-dsl")
            }
        }
    }
}


dependencies {
    api(LogLibs.slf4j)
    api(VertxLibs.core)
    api(DatabaseLibs.jooq)
    api(ZeroLibs.rql_jooq)
    compileOnly(VertxLibs.sqlClient)
    compileOnly(VertxLibs.jdbc)

    testFixturesApi(LogLibs.logback)
    testFixturesApi(TestLibs.junit5Api)
    testFixturesApi(TestLibs.junit5Engine)
    testFixturesApi(TestLibs.junit5Params)
    testFixturesApi(VertxLibs.junit5)
    testFixturesApi(TestContainers.junit5)

    testFixturesCompileOnly(UtilLibs.lombok)
    testFixturesAnnotationProcessor(UtilLibs.lombok)

    testFixturesImplementation(VertxLibs.jdbc)
    testFixturesImplementation(VertxLibs.mysql)
    testFixturesImplementation(VertxLibs.pgsql)
    testFixturesImplementation(DatabaseLibs.h2)
    testFixturesImplementation(DatabaseLibs.pgsql)
    testFixturesImplementation(DatabaseLibs.mysql)

    testFixturesImplementation(TestContainers.pgsql)
    testFixturesImplementation(TestContainers.mysql)

    testFixturesImplementation(DatabaseLibs.agroalApi)
    testFixturesImplementation(DatabaseLibs.hikari)
}

nexusStaging {
    packageGroup = "io.github.zero88"
    username = project.property("nexus.username") as String?
    password = project.property("nexus.password") as String?
}
