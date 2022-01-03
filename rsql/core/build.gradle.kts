dependencies {
    api(project(":jpa-ext"))
    api(ZeroLibs.utils)
    api(LogLibs.slf4j)
    api(DatabaseLibs.rsql)
    //For compile only to workaround RSQL annotation
    compileOnly("net.jcip:jcip-annotations:1.0")
}
