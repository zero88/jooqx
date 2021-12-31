dependencies {
    api(project(":rsql:core"))
    api(DatabaseLibs.jooq)

    testImplementation(project(":rsql:core"))
    testImplementation(testFixtures(project(":integtest")))
    testImplementation(LogLibs.logback)
    testImplementation(DatabaseLibs.jooqMeta)
    testImplementation(DatabaseLibs.jooqMetaExt)
}
