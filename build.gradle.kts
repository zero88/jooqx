import cloud.playio.gradle.NexusConfig
import cloud.playio.gradle.NexusVersion

plugins {
    eclipse
    idea
    id(PlayioPlugin.oss) version PlayioPlugin.Version.gradlePlugin
    id(PlayioPlugin.root) version PlayioPlugin.Version.gradlePlugin
    id(PlayioPlugin.antora) version PlayioPlugin.Version.gradlePlugin apply false
    id(PlayioPlugin.pandoc) version PlayioPlugin.Version.gradlePlugin apply false
    id(PlayioPlugin.codegen) version PlayioPlugin.Version.gradlePlugin apply false
    id(PlayioPlugin.docgen) version PlayioPlugin.Version.gradlePlugin apply false
    id(PluginLibs.jooq) version PluginLibs.Version.jooq apply false
}

project.ext.set("baseName", "jooqx")
project.ext.set(NexusConfig.NEXUS_VERSION_KEY, NexusVersion.BEFORE_2021_02_24)

allprojects {
    group = "io.github.zero88"

    repositories {
        mavenLocal()
        maven { url = uri("https://maven-central-asia.storage-download.googleapis.com/maven2/") }
        maven { url = uri("https://oss.sonatype.org/content/groups/public/") }
        mavenCentral()
    }
    val skipPublish = (gradle as ExtensionAware).extensions["SKIP_PUBLISH"] as Array<*>
    sonarqube {
        isSkipProject = project.path in skipPublish
    }

    tasks {
        withType<AbstractPublishToMaven> {
            enabled = project != rootProject && project.path !in skipPublish
        }
    }
}

subprojects {
    apply(plugin = PlayioPlugin.oss)

    dependencies {
        compileOnly(UtilLibs.jetbrainsAnnotations)

        testImplementation(TestLibs.junit5Api)
        testImplementation(TestLibs.junit5Engine)
        testImplementation(TestLibs.junit5Vintage)
        testImplementation(TestLibs.junitPioneer)
        testCompileOnly(UtilLibs.jetbrainsAnnotations)
    }

    oss {
        zero88.set(true)
        github.set(true)
        testLogger {
            slowThreshold = 5000
        }
    }

}

tasks.register("generateJooq") {
    group = "jooq"
    dependsOn(subprojects.map { it.tasks.withType<nu.studer.gradle.jooq.JooqGenerate>() })
}
