dependencies {
    api(project(":rsql:core"))
    api(DatabaseLibs.jooq)

    testImplementation(project(":sample:model"))
    testImplementation(DatabaseLibs.jooqMeta)
}
