oss {
    baseName.set("jpa-ext")
}

dependencies {
    compileOnlyApi(JacksonLibs.databind)
    testImplementation(JacksonLibs.databind)
}


