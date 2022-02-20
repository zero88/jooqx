oss {
    baseName.set("rsql")
}

subprojects {
    version = project.parent!!.version

    oss {
        baseName.set("${project.parent!!.oss.baseName.get()}-${project.name}")
    }

}
