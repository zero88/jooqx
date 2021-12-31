/*
 * This file was generated by the Gradle 'init' task.
 *
 * The settings file is used to specify which projects to include in your build.
 *
 * Detailed information about configuring a multi-project build in Gradle can be found
 * in the user manual at https://docs.gradle.org/6.7/userguide/multi_project_builds.html
 */

rootProject.name = "jooqx"
val profile: String by settings

include(":jpa-ext")
include(":jooqx-core")
project(":jooqx-core").projectDir = file("core")
include(":spi")

if (profile == "all") {
    include(":integtest")
    include(":docs")
    include(":rsql:core", ":rsql:jooq", ":rsql:docs")
}
