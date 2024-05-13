dependencies {
    api(projects.rsql.core)
    api(libs.jooq)

    testImplementation(libs.h2Jdbc)
    testImplementation(libs.jooqMeta)
}
