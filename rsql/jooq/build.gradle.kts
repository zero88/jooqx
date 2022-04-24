dependencies {
    api(project(":rsql:core"))
    api(JooqLibs.jooq)

    testImplementation(DatabaseLibs.h2)
    testImplementation(JooqLibs.jooqMeta)
}
