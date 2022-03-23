/*
 * This file was generated by the Gradle 'init' task.
 *
 * The settings file is used to specify which projects to include in your build.
 *
 * Detailed information about configuring a multi-project build in Gradle can be found
 * in the user manual at https://docs.gradle.org/6.7/userguide/multi_project_builds.html
 */

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}

rootProject.name = "jooqx"
val profile: String by settings
var pp: Array<String> = arrayOf()
val pools = mutableMapOf(
    "jpa" to arrayOf(":jpa-ext"),
    "jooqx" to arrayOf(":jooqx-core", ":spi"),
    "sample" to arrayOf(":sample:model", ":sample:web"),
    "rsql" to arrayOf(":rsql:core", ":rsql:jooq"),
    "integtest" to arrayOf(":integtest")
)
val jooqxDocs = arrayOf(":docs:asciidoc", ":docs:testing-asciidoc")
val rsqlDocs = arrayOf(":rsql:asciidoc")
val excludeCISonar = jooqxDocs + rsqlDocs
val excludeCIBuild = pools["sample"]!! + pools["integtest"]!! + excludeCISonar
pools.putAll(
    mapOf(
        "jooqx:docs" to pools["jooqx"]!!.plus(pools["sample"]!!).plus(jooqxDocs),
        "rsql:docs" to pools["rsql"]!!.plus(pools["sample"]!!).plus(rsqlDocs)
    )
)

fun flatten(): List<String> = pools.values.toTypedArray().flatten()

pp = when {
    profile == "all" || profile.isBlank() -> flatten().toTypedArray()
    profile == "ciBuild"                  -> flatten().filter { !excludeCIBuild.contains(it) }.toTypedArray()
    profile == "ciSonar"                  -> flatten().filter { !excludeCISonar.contains(it) }.toTypedArray()
    else                                  -> pools.getOrElse(profile) { throw RuntimeException("Not found profile[$profile]") }
}

pp.forEach { include(it) }
if (pp.contains(":jooqx-core")) {
    project(":jooqx-core").projectDir = file("core")
}

if (gradle is ExtensionAware) {
    (gradle as ExtensionAware).extensions.add("PROJECT_POOL", pools.toMap())
    (gradle as ExtensionAware).extensions.add("SKIP_PUBLISH", excludeCIBuild + arrayOf(":docs", ":rsql", ":sample"))
}
