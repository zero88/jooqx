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
        startParameter.projectProperties = mapOf("dbVersion" to dbVersion, "profile" to prop(project, "profile"))
        sub.tasks.withType<Test> {
            loadDbVersion(sub, dbVersion)
            ignoreFailures = false
        }
        finalizedBy(sub.tasks.withType<Test>())
    }
}

subprojects {
    apply(plugin = PluginLibs.jooq)

    configurations.all {
        resolutionStrategy {
            preferProjectModules()
            force(VertxLibs.core)
            force(VertxLibs.sqlClient)
            force(VertxLibs.jdbc)
        }
    }

    dependencies {
        implementation(JooqLibs.jooqMetaExt) // For generate model
        testImplementation(testFixtures(project(":jooqx")))
        testImplementation(DatabaseLibs.agroalApi)
        testImplementation(DatabaseLibs.agroalPool)
        testImplementation(DatabaseLibs.hikari)
        testImplementation(JooqLibs.jooqMeta)

        testImplementation(VertxLibs.rx2)
        testImplementation(VertxLibs.rx3)

        testImplementation(MutinyLibs.core)
        testImplementation(MutinyLibs.jdbc)

        testImplementation(LogLibs.logback)
    }

    tasks {
        withType<JooqGenerate> { allInputsDeclared.set(true) }
        register("generateJooq") {
            group = "jooq"
            dependsOn(withType<JooqGenerate>())
        }
        test { loadDbVersion(project) }
    }
}
