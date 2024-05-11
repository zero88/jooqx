dependencies {
    api(project(":rsql:core"))
    api(libs.jooq)

    testImplementation(libs.h2Jdbc)
    testImplementation(libs.jooqMeta)
}
