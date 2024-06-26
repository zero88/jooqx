import cloud.playio.gradle.NexusConfig
import cloud.playio.gradle.NexusVersion
import cloud.playio.gradle.shared.prop
import org.sonarqube.gradle.SonarQubeTask

@Suppress("DSL_SCOPE_VIOLATION") // workaround for gradle v7
plugins {
    eclipse
    idea
    id("jacoco-report-aggregation")
    alias(libs.plugins.oss)
    alias(libs.plugins.root)
    alias(libs.plugins.antora) apply false
    alias(libs.plugins.codegen) apply false
    alias(libs.plugins.docgen) apply false
    alias(libs.plugins.jooq) apply false
}

project.ext.set("baseName", "jooqx")
project.ext.set(NexusConfig.NEXUS_VERSION_KEY, NexusVersion.BEFORE_2021_02_24)
val skipPublish = (gradle as ExtensionAware).extensions["SKIP_PUBLISH"] as Array<*>

jacoco {
    toolVersion = "0.8.12"
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
        isSkipProject = project.path in skipPublish
    }

    tasks {
        withType<AbstractPublishToMaven> {
            enabled = project != rootProject && project.path !in skipPublish
        }
    }
}

subprojects {
    apply(plugin = rootProject.libs.plugins.oss.get().pluginId)
    val jvmRuntime = JavaVersion.current().majorVersion
    val jvmRelease = prop(project, "jvmRelease") as String

    if (jvmRuntime == "8") {
        configurations.all {
            resolutionStrategy {
                eachDependency {
                    val dep = this
                    if (dep.requested.module in libs.bundles.java8libs.get().map { it.module }) {
                        with(dep.requested.module.toString()) {
                            when {
                                contains("junit-pioneer") -> dep.useVersion(libs.versions.junitPioneer.jdk8.get())
                                contains("HikariCP")      -> dep.useVersion(libs.versions.hikariCP.jdk8.get())
                                contains("agroal")        -> dep.useVersion(libs.versions.agroal.jdk8.get())
                                contains("logback")       -> dep.useVersion(libs.versions.logback.jdk8.get())
                                contains("jooq")          -> dep.useVersion(libs.versions.jooq.jdk8.get())
                                else                      -> throw IllegalArgumentException("Unknown module $this")
                            }
                        }
                    }
                }
            }
        }
    }
    val buildVersion = when (jvmRuntime) {
        "8"  -> if (jvmRelease == "8") "" else "+jvm8"
        "11" -> if (jvmRelease == "11") "" else "+jvm11"
        "17" -> if (jvmRelease == "17") "" else "+jvm17"
        "21" -> if (jvmRelease == "21") "" else "+jvm21"
        else -> throw IllegalArgumentException("Unknown version $jvmRuntime")
    }
    val semanticVersion = prop(project, "semanticVersion", "")
    version = when (semanticVersion) {
        "-SNAPSHOT" -> project.version.toString().replace(semanticVersion, buildVersion + semanticVersion)
        else        -> "${project.version}$buildVersion"
    }

    dependencies {
        compileOnly(rootProject.libs.jetbrainsAnnotations)

        testCompileOnly(rootProject.libs.jetbrainsAnnotations)
        testImplementation(rootProject.libs.bundles.junit5)
        testImplementation(rootProject.libs.junitPioneer)
    }

    oss {
        zero88.set(true)
        github.set(true)
        testLogger {
            slowThreshold = 5000
        }
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(jvmRuntime))
        }
    }

    tasks {
        withType<AbstractPublishToMaven> {
            enabled = project != rootProject && project.path !in skipPublish
        }
    }
}

rootProject.apply {
    apply(plugin = "jacoco-report-aggregation")
    dependencies {
        val profile = prop(rootProject, "profile")
        val projectList = ((gradle as ExtensionAware).extensions["PROJECT_POOL"] as Map<*, Array<String>>)[profile]
        projectList?.map(project::project)?.forEach { jacocoAggregation(it) }
    }

    tasks {
        // exclude jacoco report: https://github.com/gradle/gradle/issues/13013
        withType<JacocoReport> {
            afterEvaluate {
                classDirectories.setFrom(files(classDirectories.files.map {
                    fileTree(it) {
                        exclude("io/github/zero88/sample/**")
                    }
                }))
            }
        }

        withType<SonarQubeTask> {
            dependsOn(withType<JacocoReport>())
        }
    }
}
