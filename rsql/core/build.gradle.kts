val profile: String by project.ext
val jpaLib = "io.github.zero88:jpa-ext:1.0.0" // on CI system

dependencies {
    api(if (profile == "rsql" || profile == "rsql:docs") jpaLib else project(":jpa-ext"))
    api(libs.javaUtils)
    api(libs.rsqlParser)
}
