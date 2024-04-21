dependencies {
    api(project(":rsql:core"))
    api(JooqLibs.jooq)

    testImplementation(libs.jdbcH2)
    testImplementation(JooqLibs.jooqMeta)
}
