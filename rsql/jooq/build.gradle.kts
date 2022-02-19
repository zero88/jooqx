dependencies {
    api(project(":rsql:core"))
    api(DatabaseLibs.jooq)

    testImplementation(DatabaseLibs.h2)
    testImplementation(DatabaseLibs.jooqMeta)
}
