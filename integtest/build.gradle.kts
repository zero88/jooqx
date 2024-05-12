import cloud.playio.gradle.jooq.loadDbVersion
import cloud.playio.gradle.shared.prop
import nu.studer.gradle.jooq.JooqGenerate

val itProfile: String? by project

tasks {
    register<GradleBuild>("itTest") {
        group = "verification"
        description = "Integration test by provide `-PitProfile=<it_project:db_version>`"
        if (itProfile?.isEmpty() != false) {
            return@register
        }
        val itProject: String = itProfile!!.substringBefore(":")
        val dbVersion: String = itProfile!!.substringAfter(":")
        val sub = subprojects.find { itProject == it.name }
        if (sub == null) {
            throw TaskExecutionException(
                this,
                IllegalArgumentException("Not found sub project '${project.path}:$itProject'")
            )
        }
        tasks = sub.tasks.withType<JooqGenerate>().map { "${sub.path}:${it.name}" }
        startParameter.projectProperties = mapOf(
            "dbVersion" to dbVersion,
            "profile" to prop(project, "profile")
        )
        sub.tasks.withType<Test> {
            loadDbVersion(sub, dbVersion)
            ignoreFailures = false
        }
        finalizedBy(sub.tasks.withType<Test>(), sub.tasks.withType<JacocoReport>())
    }
}

subprojects {
    apply(plugin = rootProject.libs.plugins.jooq.get().pluginId)

    configurations.all {
        resolutionStrategy {
            preferProjectModules()
            force(rootProject.libs.vertxCore)
            force(rootProject.libs.sqlClientVertx)
            force(rootProject.libs.jdbcVertx)
        }
    }

    dependencies {
        implementation(rootProject.libs.jooqMetaExt) // For generate model
        testImplementation(testFixtures(project(":jooqx")))
        testImplementation(rootProject.libs.jooqMeta)
        testImplementation(rootProject.libs.hikariCP)
        testImplementation(rootProject.libs.bundles.agroal)

        testImplementation(rootProject.libs.vertxRx2)
        testImplementation(rootProject.libs.vertxRx3)

        testImplementation(rootProject.libs.mutinyCore)
        testImplementation(rootProject.libs.jdbcMutiny)

        testImplementation(rootProject.libs.bundles.logback)
    }

    tasks {
        withType<JooqGenerate> { allInputsDeclared.set(true) }
        register("generateJooq") {
            group = "jooq"
            dependsOn(withType<JooqGenerate>())
        }
        test {
            enabled = prop(project, "skipItTest")?.toBoolean() == false
            loadDbVersion(project)
        }
        compileTestJava {
            enabled = prop(project, "skipItTest")?.toBoolean() == false
        }
    }
}
