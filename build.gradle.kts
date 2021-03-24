plugins {
    eclipse
    idea
    `java-test-fixtures`
    id(ZeroLibs.Plugins.oss) version ZeroLibs.Version.plugin
    id(ZeroLibs.Plugins.root) version ZeroLibs.Version.plugin apply false
    id(PluginLibs.jooq) version PluginLibs.Version.jooq apply false
    id(PluginLibs.nexusStaging) version PluginLibs.Version.nexusStaging
}

apply(plugin = ZeroLibs.Plugins.root)

allprojects {
    group = "io.github.zero88"

    repositories {
        mavenLocal()
        maven { url = uri("https://oss.sonatype.org/content/groups/public/") }
        mavenCentral()
        jcenter()
    }
}

subprojects {
    apply(plugin = ZeroLibs.Plugins.oss)
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
        compileOnly(UtilLibs.jetbrainsAnnotations)
        compileOnly(UtilLibs.lombok)
        annotationProcessor(UtilLibs.lombok)

        testImplementation(TestLibs.junit5Api)
        testImplementation(TestLibs.junit5Engine)
        testImplementation(TestLibs.junit5Vintage)
        testCompileOnly(UtilLibs.lombok)
        testAnnotationProcessor(UtilLibs.lombok)
    }

    qwe {
        zero88.set(true)
        publishingInfo {
            enabled.set(true)
            homepage.set("https://github.com/zero88/jooqx")
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://github.com/zero88/jooqx/blob/master/LICENSE")
            }
            scm {
                connection.set("scm:git:git://git@github.com:zero88/jooqx.git")
                developerConnection.set("scm:git:ssh://git@github.com:zero88/jooqx.git")
                url.set("https://github.com/zero88/jooqx")
            }
        }
    }
}


nexusStaging {
    packageGroup = "io.github.zero88"
    username = project.property("nexus.username") as String?
    password = project.property("nexus.password") as String?
}

tasks.register("generateJooq") {
    group = "jooq"
    dependsOn(subprojects.map { it.tasks.withType<nu.studer.gradle.jooq.JooqGenerate>() })
}
