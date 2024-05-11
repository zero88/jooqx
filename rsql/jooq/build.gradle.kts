dependencies {
    api(project(":rsql:core"))
    api(JooqLibs.jooq)

    testImplementation(libs.h2Jdbc)
    testImplementation(JooqLibs.jooqMeta)
}
