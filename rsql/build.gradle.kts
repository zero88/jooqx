qwe {
    baseName.set("rsql")
}

subprojects {
    version = project.parent!!.version

    qwe {
        baseName.set("${project.parent!!.qwe.baseName.get()}-${project.name}")
    }

}
