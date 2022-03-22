plugins {
    eclipse
    idea
    id(ZeroLibs.Plugins.oss) version ZeroLibs.Version.gradlePlugin
    id(ZeroLibs.Plugins.root) version ZeroLibs.Version.gradlePlugin

    id(PluginLibs.jooq) version PluginLibs.Version.jooq apply false
    id(PluginLibs.nexusPublish) version PluginLibs.Version.nexusPublish
}

allprojects {
    group = "io.github.zero88"

    repositories {
        mavenLocal()
        maven { url = uri("https://maven-central-asia.storage-download.googleapis.com/maven2/") }
        maven { url = uri("https://oss.sonatype.org/content/groups/public/") }
        mavenCentral()
    }

    sonarqube {
        isSkipProject = project.name.contains("sample")
    }

    tasks {
        withType<AbstractPublishToMaven> {
            enabled = project != rootProject && project.name !in arrayOf("rsql", "integtest", "sample", "docs")
        }
    }
}

subprojects {
    apply(plugin = ZeroLibs.Plugins.oss)
    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    dependencies {
        compileOnly(UtilLibs.jetbrainsAnnotations)
        compileOnly(UtilLibs.lombok)
        annotationProcessor(UtilLibs.lombok)

        testImplementation(TestLibs.junit5Api)
        testImplementation(TestLibs.junit5Engine)
        testImplementation(TestLibs.junit5Vintage)
        testCompileOnly(UtilLibs.jetbrainsAnnotations)
        testCompileOnly(UtilLibs.lombok)
        testAnnotationProcessor(UtilLibs.lombok)
    }

    oss {
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
        testLogger {
            slowThreshold = 5000
        }
    }

}


nexusPublishing {
    packageGroup.set("io.github.zero88")
    repositories {
        sonatype {
            username.set(project.property("nexus.username") as String?)
            password.set(project.property("nexus.password") as String?)
        }
    }
}

tasks.register("generateJooq") {
    group = "jooq"
    dependsOn(subprojects.map { it.tasks.withType<nu.studer.gradle.jooq.JooqGenerate>() })
}
