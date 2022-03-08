val profile: String by project.ext
val jpaLib = "io.github.zero88:jpa-ext:1.1.0-SNAPSHOT" // on CI system. Version 1.1.0-SNAPSHOT is temporary

dependencies {
    api(if (profile == "rsql") jpaLib else project(":jpa-ext"))
    api(LogLibs.slf4j)
    api(ZeroLibs.utils)
    api(ZeroLibs.rsql)
}
